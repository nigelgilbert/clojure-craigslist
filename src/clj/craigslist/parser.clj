(ns craigslist.parser
  (:require [clojure.java.io :as io]
            [hickory.core :as hickory]
            [hickory.select :as hs]
            [clojure.string :as str]
            [craigslist.util :as util]))

(def re-qualified-url (re-pattern #"/^\/\/[a-z0-9\-]*\.craigslist\.[a-z]*/"))
(def re-html (re-pattern #"(?i).htm(l)?"))
(def re-tags-map (re-pattern #"/map/i"))

;; test utils
;; TODO: remove
(defn ref-parser []
  (use 'craigslist.parser :reload))

(defn get-markdown []
  (slurp (io/resource "listing.html")))

(def selector fn []
  (hs/child (hs/tag :body)))
          
(defn select-listings [hiccup-str]
  (-> (hs/select (selector) hiccup-str)))

(defn parse-listings [{:keys [status headers body error]}]
  (println
    (select-listings
      (hickory/parse body))))

; (-> (s/select (s/child (s/class "subCalender") ; sic
;                               (s/tag :div)
;                               (s/id :raceDates)
;                               s/first-child
;                               (s/tag :b))
;                      site-htree)
;            first :content first string/trim)