/** 校区枚举 */
export const CAMPUS_OPTIONS = [
  '仙林校区',
  '鼓楼校区',
  '浦口校区',
  '苏州校区',
]

/** 年级枚举 */
export const GRADE_OPTIONS = [
  '大一',
  '大二',
  '大三',
  '大四',
  '研究生',
  '博士生',
  '其他',
]

/** 帖子类型 */
export const POST_TYPE_OPTIONS = [
  '自习搭子',
  '运动搭子',
  '竞赛组队',
  '课程学习',
  '拼单',
  '饭搭子',
  '短途出行',
  '讲座同行',
  '社团活动',
  '跨校区同行',
  '其他',
]

/** 帖子状态 */
export const POST_STATUS = {
  RECRUITING: 0,
  FULL: 1,
  CLOSED: 2,
  EXPIRED: 3,
}

export const POST_STATUS_MAP = {
  0: '招募中',
  1: '已成团',
  2: '已关闭',
  3: '已过期',
}

/** 报名状态 */
export const APPLICATION_STATUS = {
  PENDING: 0,
  PASSED: 1,
  REJECTED: 2,
  CANCELLED: 3,
}

export const APPLICATION_STATUS_MAP = {
  0: '待审核',
  1: '已通过',
  2: '已拒绝',
  3: '已取消',
}
