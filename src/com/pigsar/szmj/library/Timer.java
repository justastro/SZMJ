//package com.pigsar.szmj.library;
//
//import com.pigsar.szmj.graphic.AnimationEventListener;
//
//public class Timer {
//	private float _endTime;
//	private float _time;
//	
//	public void update(float time) {
//		if (_endTime == Float.NEGATIVE_INFINITY) return;
//		
//		_time += time;
//		float ratio = _time / _endTime;
//		if (ratio >= 1) {
//			ratio = 1;
//			_endTime = Float.NEGATIVE_INFINITY;
//			for (AnimationEventListener listener : _listeners) {
//				listener.onAnimationEnded();
//			}
//		}
//	}
//}
