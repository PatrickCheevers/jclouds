(ns org.jclouds.blobstore-test
  (:use [org.jclouds.blobstore] :reload-all)
  (:use [clojure.test])
  (:import [org.jclouds.blobstore BlobStoreContextFactory]
           [java.io ByteArrayOutputStream]))

(def properties (let [p (java.util.Properties.)]
                  (with-open [properties-file (.getResourceAsStream
                             (clojure.lang.RT/baseLoader)
                             "blobstore-credentials.properties")]
                    (.load p properties-file))
                  (into {} p)))

(defn blobstore-fixture
  "This should allow basic tests to easily be run with another service."
  [f]
  (with-blobstore [(apply blobstore (map properties
                                         ["org.jclouds.blobstore.service"
                                          "org.jclouds.blobstore.account"
                                          "org.jclouds.blobstore.key"]))]
    (f)))

(use-fixtures :each blobstore-fixture)

(deftest blobstore?-test
  (is (blobstore? *blobstore*)))

(deftest as-blobstore-test
  (is (blobstore? (blobstore "transient" "user" "password")))
  (is (blobstore? (as-blobstore *blobstore*)))
  (is (blobstore? (as-blobstore (blobstore-context *blobstore*)))))

(deftest create-existing-container-test
  (doseq [container (containers)]
    (delete-container (.getName container)))
  (is (empty? (containers)))
  (is (not (container-exists? *blobstore* "")))
  (is (not (container-exists? "")))
  (is (create-container *blobstore* "fred"))
  (is (container-exists? *blobstore* "fred")))

(deftest create-container-test
  (is (create-container *blobstore* "fred"))
  (is (container-exists? *blobstore* "fred")))

(deftest containers-test
  (delete-container "fred")
  (let [c (containers)]
    (is (create-container *blobstore* "fred"))
    (is (= (inc (count c)) (count (containers))))))

(deftest list-container-test
  (is (create-container "container"))
  (is (empty? (list-container "container")))
  (is (create-blob "container" "blob1" "blob1"))
  (is (create-blob "container" "blob2" "blob2"))
  (is (= 2 (count (list-container "container"))))
  (is (= 1 (count (list-container "container" :max-results 1))))
  (create-directory "container" "dir")
  (is (create-blob "container" "dir/blob2" "blob2"))
  (is (= 3 (count (list-container "container"))))
  (is (= 4 (count (list-container "container" :recursive))))
  (is (= 1 (count (list-container "container" :in-directory "dir")))))

(deftest download-blob-test
  (let [name "test"
        container-name "test-container"
        data "test content"
        data-file (java.io.File/createTempFile "jclouds" "data")]
    (try (create-container container-name)
         (create-blob container-name name data)
         (download-blob container-name name data-file)
         (is (= data (slurp (.getAbsolutePath data-file))))
         (finally (.delete data-file)))))

(deftest download-checksum-test
  (binding [get-blob (fn [blobstore c-name name]
                       (let [blob (.newBlob blobstore name)
                             md (.getMetadata blob)]
                         (.setPayload blob "bogus payload")
                         (.setContentMD5 md (.getBytes "bogus MD5"))
                         blob))]
    (let [name "test"
          container-name "test-container"
          data "test content"
          data-file (java.io.File/createTempFile "jclouds" "data")]
      (try (create-container container-name)
           (create-blob container-name name data)
           (is (thrown? Exception
                        (download-blob container-name name data-file)))
           (finally (.delete data-file))))))

;; TODO: more tests involving blob-specific functions

(deftest corruption-hunt
  (let [container-name (properties "org.jclouds.blobstore.container-name")
        name "work-file"
        total-downloads 100
        threads 10]

    ;; upload
    (create-container container-name)
    (when-not (blob-exists? container-name name)
      (let [data-stream (java.io.ByteArrayOutputStream.)]
        (dotimes [i 5000000] (.write data-stream i))
        (create-blob container-name name (.toByteArray data-stream))))

    ;; download
    (let [total (atom total-downloads)]
      (defn new-agent []
        (agent name))

      (defn dl-and-restart [blob-s file]
        (when-not (<= @total 0)
          (with-open [baos (java.io.ByteArrayOutputStream.)]
            (try
             (download-blob blob-s container-name file baos)
             (catch Exception e
               (with-open [of (java.io.FileOutputStream.
                               (java.io.File/createTempFile "jclouds" ".dl"))]
                 (.write of (.toByteArray baos)))
               (throw e))))
          (swap! total dec)
          (send *agent* (partial dl-and-restart blob-s))
          file))

      (defn start-agents []
        (let [agents (map (fn [_] (new-agent))
                          (range threads))]
          (doseq [a agents]
            (send-off a (partial dl-and-restart *blobstore*)))
          agents))

      (let [agents (start-agents)]
        (apply await agents)
        (is (every? nil? (map agent-errors agents)))))))
