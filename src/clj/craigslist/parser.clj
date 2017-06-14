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
(def select-first (comp hs/select first))

;; test utils
;; TODO: remove
(defn ref-parser []
  (use 'craigslist.parser :reload))

;; filename = any file from resources/public
(defn get-markdown [filename]
  (slurp (io/resource filename)))

(defn extract-posting-title [row-element]
  (->> (hs/select (hs/child
         (hs/class :result-title)) row-element)
       (first)
       (:content)
       (map #(hash-map :tile %))
       (first)))

(defn select-search-postings [hickory-tree]
  (->> (hs/select (hs/child
         (hs/class :result-row)) hickory-tree)
       (map extract-posting-title)))

(defn test-parse-search-results []
  (select-search-postings (parse-as-hickory
    (get-markdown "search-results.html"))))

(defn get-posting-description [htree]
  (-> (hs/select (hs/child
        (hs/id :postingbody)
        (hs/not (hs/node-type :element))) htree)
      (str/join)
      (str/trim)))

(defn get-posting-map-url [htree]
  (-> (hs/select (hs/descendant
        (hs/class :mapbox)
        (hs/class :mapaddress)
        (hs/tag :a)) htree)
      (first)
      (get-in [:attrs :href])))
  
(defn get-posting-reply-url [htree]
  (-> (hs/select (hs/descendant
        (hs/id :replylink)) htree)
      (first)
      (get-in [:attrs :href])))

;; TODO: parse this
(defn get-posting-info-div [htree]
  (-> (hs/select (hs/descendant
        (hs/class :postinginfos)) htree)
      (first)
      (get :content)))

(defn test-parse-posting []
    (get-posting-info-div
      (parse-as-hickory
        (get-markdown "posting.html"))))

  