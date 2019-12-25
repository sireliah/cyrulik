(ns cyrulik.test.filters
  (:require
    [clojure.test :refer :all]
    [cyrulik.filters :refer :all]))

(deftest test-app
  (testing "replace-all"
    (let [vec '("http://su.cat" "http://dsl.cz" "http://a.com")
          target "some text http://su.cat another http://a.com"
          expected "some text <a href=http://su.cat target=_blank>http://su.cat</a> another <a href=http://a.com target=_blank>http://a.com</a>"]
      (is (= (replace-all vec target) expected)))))
