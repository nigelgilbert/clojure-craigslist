(ns craigslist.client
  (:require [clojure.java.io :as io]
            [hickory.core :as hickory]
            [org.httpkit.client :as http]))

(def default-config {
  :city "dallas" 
  :base-host "craigslist.org"
  :category "sss"
  :category-details-index 1,
  :path "/search",
  :request-options {
    :host ""
	:path ""
	:secure true
  }
  :query-keys [
    "category"
    "maxAsk"
    "minAsk"
  ]
  :query-base "?sort=rel"
  :query-param-max "&maxAsk="
  :query-param-min "&minAsk="
  :query-param-query "&query="
})

;; parses a markdown string to hiccup
(defn parse-markdown [html-str]
  (hickory/as-hiccup
    (hickory/parse html-str)))

(defn get-markdown [] 
   (slurp (io/resource "listing.html")))

;; regular expressions
(def re-qualified-url (re-pattern #"/^\/\/[a-z0-9\-]*\.craigslist\.[a-z]*/"))
(def re-html (re-pattern #"(?i).htm(l)?"))
(def re-tags-map (re-pattern #"/map/i"))

;; returns query config
(defn make-config [options]
  (merge default-config options))

;; returns listings
(defn search [options query-str]
  (let [config (make-config options)]
    (println config)))

;; returns an edn listing object
; (get-posting-details [hiccup-str]
;   ())

;; test utils
;; TODO: remove
(defn ref-src []
  (use 'craigslist.client :reload))

(defn test-parse [] 
  (parse-markdown (get-markdown)))


