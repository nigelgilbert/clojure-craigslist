(ns craigslist.test
  (:require [clojure.test :refer :all]
            [craigslist.client :as craigslist]))

;; (?i).htm(l)?
 (deftest re-html-test
   (is (= ".html" (first (re-find craigslist/re-html "test.html"))))
   (is (= ".htm" (first (re-find craigslist/re-html "test.htm"))))
   (is (not= ".html" (first (re-find craigslist/re-html "test.ojiw")))))



;; test utils
;; TODO: remove
(defn run-test [] 
  (run-tests 'clojure-craigslist.craigslist-test))

(defn ref-test []
  (use 'clojure-craigslist.craigslist-test :reload))