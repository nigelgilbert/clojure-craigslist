(ns craigslist.client
  (:require [clojure.java.io :as io]
            [hickory.core :as hickory]
            [org.httpkit.client :as http]
            [clojure.string :as str]
            [craigslist.parser :as parser]
            [craigslist.util :as util]))

;; User configurable options
(def default-options {
  :category "sss"
  :city "dallas"
  :index 1,
  :min ""
  :max ""
  :query ""
})

;; Values used to create request URL
(def default-config {
  :request {
    :hostname ""
    :path ""
    :secure true
  }
  :base-host "craigslist.org"
  :path "/search/"
  :query-str "?sort=rel"
  :query-param-max "&maxAsk="
  :query-param-min "&minAsk="
  :query-param-query "&query="
})

;; test utils
;; TODO: remove
(defn ref-src []
  (use 'craigslist.client :reload))

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
  (assoc-in config [:request :hostname]
    (str "https://" (config :city) "." (config :base-host))))

(defn make-request-path [config]
  (assoc-in config [:request :path]
    (str
      (config :path)
      (config :category)
      (try-make-query config)
      (try-make-min-query config)
      (try-make-max-query config))))

(defn setup-request-options [config]
  (-> (make-request-hostname config)
      (make-request-path)))

(defn make-url [config]
  (str/join
    (util/select-values
      (config :request) [:hostname :path])))

(defn get-craigslist-listings [config callback]
  (let [url (make-url config)]
    (http/get url {} callback)))

;; Client API
;; returns listings
(defn search [options]
  (let [config (merge default-config default-options options)]
    (-> (setup-request-options config)
        (make-url))))
        ; (get-craigslist-listings parser/parse-listings))))