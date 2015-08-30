(ns figwheel-one.core
  (:require [quil.core :as q :include-macros true] [quil.middleware :as m]))

(enable-console-print!)

(defn setup [] 
  (q/smooth)
  (q/frame-rate 30)
  {:width 100
   :height 100
   :items 1000
   :snake-x 0
   :snake-y 0
   :direction :up})

(defn get-xy-sizes [{width :width height :height}]
  (vector (/ (q/width) width) (/ (q/height) height)))

(defn get-xy-position [{:keys [snake-x snake-y width height direction]}]
  (case direction
    :up    [snake-x (mod (- snake-y (/ (q/height) height)) (q/height))]
    :down  [snake-x (mod (+ snake-y (/ (q/height) height)) (q/height))]
    :left  [(mod (- snake-x (/ (q/width) width)) (q/width)) snake-y]
    :right [(mod (+ snake-x (/ (q/width) width)) (q/width)) snake-y]
    ))

(defn draw-snake [state]
  (q/fill 255)
  (let [[xs ys] (get-xy-sizes state)]
    (q/rect (:snake-x state) (:snake-y state) xs ys)))

(defn draw-state [state]
  (q/background 0)
  (draw-snake state))

(defn update-state [state] 
  (let [{:keys [snake-x snake-y width height items direction]} state
        [new-xp new-yp] (get-xy-position state)]
    {:snake-x new-xp
     :snake-y new-yp
     :width width
     :height height
     :items items
     :direction direction}))

(defn on-key-down [state event]
  (case (:key event)
    :up (assoc-in state [:direction] :up)
    :down (assoc-in state [:direction] :down)
    :left (assoc-in state [:direction] :left)
    :right (assoc-in state [:direction] :right)
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
