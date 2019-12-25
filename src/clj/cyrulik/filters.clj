(ns cyrulik.filters 
  (:use selmer.filters))

(def pattern #"https?:\/\/(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b[-a-zA-Z0-9@:%_\+.~#?&//=]*")

(defn format-url [s target]
  "Converts given string to the HTML URL"
  (clojure.string/replace target s (format "<a href=%s target=_blank>%s</a>" s s)))

(defn replace-all [to-replace-list target]
  (if (empty? to-replace-list)
    target
    (let [first-elem (first to-replace-list)]
      (replace-all (pop to-replace-list) (format-url first-elem target)))))

(defn make-url [value]
  (let [matched (apply list (re-seq pattern value))]
    (if matched
      (replace-all matched value)
      value)))

(add-filter! :makeurl make-url)
