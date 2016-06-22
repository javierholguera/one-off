(ns photorg.core
  (:require [clj-time.coerce :as tc]
            [clj-time.format :as tf]
            [clojure.java.io :as io])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn modified-at
  [file]
  (tf/unparse (tf/formatters :date)
              (tc/from-long (.lastModified file))))

(defn extension
  [file]
  (last (clojure.string/split (.getName file)
                              #"\.")))

(defn all-photos
  [path]
  (filter (fn [f]
            (and (not (.isDirectory f))
                 (= "clj" (extension f))))
          (file-seq (java.io.File. path))))

(defn- dest-folder
  [photo]
  (java.io.File.
   (str "/tmp/photorg/"
        (:modified-at photo)
        "/"
        (:parent photo)
        "/")))

(defn- copy-rightly!
  [photos]
  (doseq [photo photos]
    (let [dest-folder (dest-folder photo)]
      (when-not (.exists dest-folder)
        (.mkdirs dest-folder))
      (io/copy (:file photo)
               (java.io.File. (str (.getAbsolutePath dest-folder) "/" (:name photo)))))))


(defn photos
  [path]
  (map (fn [f]
         {:name (.getName f)
          :path (.getAbsolutePath f)
          :parent (.getName (.getParentFile f))
          :modified-at (modified-at f)
          :file f})
       (all-photos path)))

(defn do-stuff
  [path]
  (copy-rightly! (photos path)))

