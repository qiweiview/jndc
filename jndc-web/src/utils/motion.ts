import type { Variants, Transition } from 'framer-motion';

/** 通用过渡配置 */
const defaultTransition: Transition = {
  duration: 0.25,
  ease: [0.4, 0, 0.2, 1],
};

/** 淡入 */
export const fadeVariants: Variants = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: defaultTransition },
  exit: { opacity: 0, transition: { ...defaultTransition, duration: 0.15 } },
};

/** 向上滑入 */
export const slideUpVariants: Variants = {
  initial: { y: 20, opacity: 0 },
  animate: { y: 0, opacity: 1, transition: defaultTransition },
  exit: { y: 20, opacity: 0, transition: { ...defaultTransition, duration: 0.15 } },
};

/** 缩放弹入 */
export const scaleVariants: Variants = {
  initial: { scale: 0.96, opacity: 0 },
  animate: { scale: 1, opacity: 1, transition: defaultTransition },
  exit: { scale: 0.96, opacity: 0, transition: { ...defaultTransition, duration: 0.15 } },
};

/** 列表 stagger 容器 */
export const staggerContainerVariants: Variants = {
  initial: {},
  animate: {
    transition: {
      staggerChildren: 0.05,
      delayChildren: 0.05,
    },
  },
};

/** 列表子项动画 */
export const staggerItemVariants: Variants = {
  initial: { y: 12, opacity: 0 },
  animate: {
    y: 0,
    opacity: 1,
    transition: defaultTransition,
  },
};

/** hover 缩放（用于可点击元素） */
export const hoverScale = {
  whileHover: { scale: 1.03, transition: { duration: 0.15 } },
  whileTap: { scale: 0.98 },
};

/** hover 轻微浮起 */
export const hoverLift = {
  whileHover: {
    y: -2,
    boxShadow: '0 4px 16px rgba(0, 0, 0, 0.08)',
    transition: { duration: 0.15 },
  },
};

/** 页面切换动画（用于路由） */
export const pageTransitionVariants: Variants = {
  initial: { opacity: 0, y: 12 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.3, ease: [0.4, 0, 0.2, 1] },
  },
  exit: {
    opacity: 0,
    y: -8,
    transition: { duration: 0.2, ease: [0.4, 0, 0.2, 1] },
  },
};
