(ns craigslist.parser
  (:require [clojure.java.io :as io]
            [hickory.core :as hickory]
            [hickory.select :as hs]
            [clojure.string :as str]
            [craigslist.util :as util]))

(def re-qualified-url (re-pattern #"/^\/\/[a-z0-9\-]*\.craigslist\.[a-z]*/"))
(def re-html (re-pattern #"(?i).htm(l)?"))
(def re-tags-map (re-pattern #"/map/i"))

;; utils 
(def parse-as-hickory (comp hickory/as-hickory hickory/parse))
(def select-child (fn [htree & selectors] 
  (hs/select (hs/child ))))

;; test utils
;; TODO: remove
(defn ref-parser []
  (use 'craigslist.parser :reload))

;; filename = any file from resources/public
(defn get-markdown [filename]
  (slurp (io/resource filename)))

(defn select-search-results [hickory-tree]
  (hs/select (hs/child
    (hs/tag :div)) hickory-tree))

(defn test-parse-search-results []
  (println
    (select-search-results (parse-as-hickory
      (get-markdown "search-results.html")))))

(defn get-listing-description [htree]
  (-> (hs/select (hs/child
        (hs/id :postingbody)
        (hs/not (hs/node-type :element))) htree)
      (str/join)
      (str/trim)))

(defn get-listing-map-url [htree]
  (-> (hs/select (hs/descendant
        (hs/class :mapbox)
        (hs/class :mapaddress)
        (hs/tag :a)) htree)
      (first)
      (get-in [:attrs :href])))
  
(defn get-listing-reply-url [htree]
  (-> (hs/select (hs/descendant
        (hs/id :replylink)) htree)
      (first)
      (get-in [:attrs :href])))

(defn test-parse-listing []
    (get-listing-reply-url
      (parse-as-hickory
        (get-markdown "listing.html"))))