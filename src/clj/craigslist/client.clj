(ns craigslist.client
  (:require [clojure.java.io :as io]
            [hickory.core :as hickory]
            [org.httpkit.client :as http]
            [clojure.string :as str]
            [craigslist.util :as util]))

(def default-config {
  :base-host "craigslist.org"
  :category "sss"
  :category-details-index 1,
  :city "dallas"
  :path "/search/"
  :request-options {
    :hostname ""
    :path ""
    :secure true
  }
  :query-keys [
    "category"
    "maxAsk"
    "minAsk"
  ]
  :min ""
  :max ""
  :query ""
  :query-str "?sort=rel"
  :query-param-max "&maxAsk="
  :query-param-min "&minAsk="
  :query-param-query "&query="
})

(def re-qualified-url (re-pattern #"/^\/\/[a-z0-9\-]*\.craigslist\.[a-z]*/"))
(def re-html (re-pattern #"(?i).htm(l)?"))
(def re-tags-map (re-pattern #"/map/i"))

;; for testing
;; TODO: delete
(defn get-markdown [] 
  (slurp (io/resource "listing.html")))

;; parses a markdown string to hiccup
(defn parse-markdown [html]
  (hickory/as-hiccup
    (hickory/parse html)))

(defn make-query [config]
  (str/join
    (util/select-values config [:query-str :query-param-query :query])))

(defn try-make-query [config]
  (if (str/blank? (config :query))
    (config :query-str)
    (make-query)))

(defn try-make-min-query [config]
  (if-not (str/blank? (config :min))
    (str/join
      (util/select-values config [:query-param-min :min]))))

(defn try-make-max-query [config]
  (if-not (str/blank? (config :max))
    (str/join
      (util/select-values config [:query-param-max :max]))))

(defn make-request-hostname [config]
  (assoc-in config [:request-options :hostname]
    (str "https://" (config :city) "." (config :base-host))))

;; returns a request path string
(defn make-request-path [config]
  (assoc-in config [:request-options :path]
    (str
      (config :path)
      (config :category)
      (try-make-query config)
      (try-make-min-query config)
      (try-make-max-query config))))

;; mutates :request-options of config and returns a new config
(defn make-request-options [config]
  (-> (make-request-hostname config)
      (make-request-path)))

(defn make-url [config]
  (str/join
    (util/select-values
      (config :request-options) [:hostname :path])))

(defn try-parse-listings [{:keys [status headers body error]}]
  (println (parse-markdown body)))

(defn get-craigslist-listings [config callback]
  (let [url (make-url config)]
    (http/get url {} callback)))

;; test utils
;; TODO: remove
(defn ref-src []
  (use 'craigslist.client :reload))

(defn print-request-options [config]
  (println (config :request-options)))

;; Client API
;; returns listings
(defn search [options]
  (let [config (merge default-config options)]
    (-> (make-request-options config)
        (get-craigslist-listings try-parse-listings))))