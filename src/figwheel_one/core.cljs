(ns figwheel-one.core
  (:require [quil.core :as q :include-macros true] [quil.middleware :as m]))

(enable-console-print!)

(defn init-test-snake [count]
  (for [i (range count)
        :let [nx (* i 5)]]
    {:xp nx :yp 0}))

(defn init-snake [width height]
  ;[{:xp (/ width 2) :yp (/ height 2)}]
  (init-test-snake 30)
  )

(defn setup [] 
  (q/smooth)
  (q/frame-rate 30)
  {:width 100
   :height 100
   :items 1000
   :snake (init-snake (q/width) (q/height))
   :block-xs (/ (q/width) 100)
   :block-ys (/ (q/height) 100)
   :direction :left})

(defn get-xy-sizes [{width :width height :height}]
  (vector (/ (q/width) width) (/ (q/height) height)))

(defn get-xy-position [{:keys [block-xs block-ys direction]} {xp :xp yp :yp}]
  (case direction
    :up    {:xp xp :yp (mod (- yp block-ys) (q/height))}
    :down  {:xp xp :yp (mod (+ yp block-ys) (q/height))}
    :left  {:xp (mod (- xp block-xs) (q/width)) :yp yp}
    :right {:xp (mod (+ xp block-xs) (q/width)) :yp yp}
    ))

(defn get-xy-movement [{:keys [width height direction block-xs block-ys]}]
  (case direction
    :up    [0 (- block-ys)]
    :down  [0 block-ys]
    :left  [block-xs 0]
    :right [(- block-xs) 0]))

(defn move-snake [nx ny h & tail]
  (if tail
    [{:xp nx :yp ny} (move-snake (:xp h) (:yp h) tail)]
    {:xp nx :yp ny}))

(defn draw-snake [state]
  (q/fill 255)
  (let [[xs ys] (get-xy-sizes state)]
    (doseq [item (:snake state)] 
      (q/rect (:xp item) (:yp item) xs ys))))

(defn draw-state [state]
  (q/background 0)
  (draw-snake state))

(defn update-state [state] 
  (let [{:keys [snake width height items direction block-xs block-ys]} state]
    {:snake (cons (get-xy-position state (first (:snake state))) (drop-last (:snake state))) 
     :width width
     :height height
     :items items
     :direction direction
     :block-xs block-xs
     :block-ys block-ys}))

(defn on-key-down [state event]
  (case (:key event)
    :up (if (not= (:direction state) :down) (assoc-in state [:direction] :up) state)
    :down (if (not= (:direction state) :up) (assoc-in state [:direction] :down) state)
    :left (if (not= (:direction state) :right) (assoc-in state [:direction] :left) state)
    :right (if (not= (:direction state) :left) (assoc-in state [:direction] :right) state)
    state))

(defn on-key-up [state]
  state)

(q/defsketch hello-quil
  :host "hello-quil"
  :size [500 500]
  :draw draw-state
  :setup setup
  :update update-state
  :key-pressed on-key-down
  :key-released on-key-up
  :middleware [m/fun-mode])

(defn on-js-reload [] )
