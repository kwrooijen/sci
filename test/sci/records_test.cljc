(ns sci.records-test
  (:require [clojure.test :refer [deftest is testing]]
            [sci.test-utils :as tu]))

(deftest protocol-test
  (let [prog "
(defrecord Foo [a b])
(let [r (->Foo 1 2)]
  [(:a r) (:b r)])"]
    (is (= [1 2] (tu/eval* prog {}))))
  (testing "protocols"
    (let [prog "
(ns foo)
(defprotocol Foo (foo [_] 42))
(defprotocol Graph (graph [_]))

(ns bar (:require [foo :as f]))
(defrecord FooRecord [a b]
  f/Foo (foo [_] (dec a)))

(defrecord BarRecord [a b]
  f/Foo (foo [_] (inc b)))

(extend FooRecord
  f/Graph {:graph (fn [x] {:from (:a x) :to (:b x)})})

(let [a (->FooRecord 1 2) b (BarRecord. 1 2)]
  [(f/foo a) (f/foo b) (f/graph a) (satisfies? f/Graph a)])"]
      (is (= [0 3 {:from 1, :to 2} true] (tu/eval* prog {}))))))

(deftest extends-test
  (let [prog "
(defprotocol Area (get-area [this]))
(defrecord Rectangle [width height]
                  Area
                  (get-area [this]
                    (* width height)))
(extends? Area Rectangle)"]
    (is (true? (tu/eval* prog {})))))

(deftest instance-test
  (let [prog "
(defrecord Rectangle [width height])
(def x (Rectangle. 0 0))
(instance? Rectangle x)"]
    (is (true? (tu/eval* prog {})))))

(deftest record?-test
  (let [prog "
(defrecord Rectangle [width height])
(record? (->Rectangle 0 0))"]
    (is (true? (tu/eval* prog {})))))

;; TODO:
#_(deftest import-test
  (let [prog "
(ns foo)
(defrecord Rectangle [width height])

(ns bar
  (:require [foo :refer [->Rectangle]])
  (:import [foo.Rectangle]))

(instance? Rectangle (->Rectangle 10 20 ))"]
    (is (true? (tu/eval* prog {})))))
