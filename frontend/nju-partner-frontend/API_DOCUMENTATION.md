NJU-Partner 前端接口文档
=====================

说明
----
- 基本前缀：所有接口以 `/api` 开头。
- 授权：受保护接口需在请求头中带上 Authorization: `Bearer <token>`。
  - 未登录或 token 无效将返回 code = 401（ResultCode.UNAUTHORIZED）。
- 返回格式：所有接口都使用统一响应结构 `Result<T>`：
  ```json
  {
    "code": 200,    // 参考 ResultCode
    "message": "success",
    "data": { ... }
  }
  ```
  常见 code：200 success；400 bad request；401 unauthorized；403 forbidden；404 not found；500 error。
- 时间格式：后端在 DTO 上支持 `"yyyy-MM-dd HH:mm:ss"`，也能接受 ISO_LOCAL_DATE_TIME（例如：2026-05-31T15:00:00）。PostQueryDTO 标注时区为 GMT+8（JSON 输出时）。
- 分页：后端使用 MyBatis-Plus 的 IPage；常用查询参数 `pageNum`（默认1）、`pageSize`（默认10）。返回的 `data` 中常见字段：`records`（数组）、`total`、`current`、`size`。

公共模型（重要字段说明）
----------------------
- PostCreateDTO
  - title (String, 必填, <=100)
  - type (String, 必填)
  - description (String)
  - location (String)
  - activityTime (LocalDateTime, 必填) — 格式如 `yyyy-MM-dd HH:mm:ss`
  - needCount (Integer, 必填, >=1)
  - campus (String, 必填)
  - gradeLimit, majorLimit, contact

- PostQueryDTO（查询参数）
  - campus, type, keyword, status, grade
  - startTime, endTime （LocalDateTime，支持 `yyyy-MM-dd HH:mm:ss` 或 ISO）
  - pageNum, pageSize

- UserRegisterDTO
  - username (必填, 3-50)
  - password (必填, 6-100)
  - nickname, campus, grade, major（均为必填）

- UserLoginDTO
  - username, password

- CommentCreateDTO
  - content (必填, <=500)

- ApplicationCreateDTO
  - message (<=255)

常用返回对象
-------------
- PostVO
  - id, publisher (UserVO), title, type, description, activityTime, needCount, campus, status, createdTime...
- UserVO
  - id, username, nickname, avatar, campus, grade, major, role
- LoginVO
  - token, userInfo (UserVO)

API 列表（示例请求/响应）
------------------------

1) 用户相关

- 注册
  - POST /api/user/register
  - Body (application/json): UserRegisterDTO
  - 成功：{ code:200 }
  - 示例：
    {
      "username":"alice",
      "password":"password1",
      "nickname":"Alice",
      "campus":"仙林校区",
      "grade":"大一",
      "major":"计算机"
    }

- 登录
  - POST /api/user/login
  - Body: UserLoginDTO
  - 成功返回 LoginVO
  - 示例响应 data:
    {
      "token":"eyJ...",
      "userInfo": { "id":1, "username":"alice", "nickname":"Alice" }
    }

- 获取当前用户信息
  - GET /api/user/profile
  - Header: Authorization: Bearer <token>
  - 返回 UserVO

- 我的发布
  - GET /api/user/my-posts
  - Header: Authorization
  - 返回 PostVO 列表

- 我的申请
  - GET /api/user/my-applications
  - Header: Authorization
  - 返回 ApplicationVO 列表

- 我的收藏
  - GET /api/user/my-favorites
  - Header: Authorization
  - 返回 PostVO 列表


2) 帖子（Post）

- 创建帖子
  - POST /api/posts
  - Header: Authorization
  - Body: PostCreateDTO
  - 成功：{ code:200 }
  - 示例 cURL:
    ```bash
    curl -X POST "https://<host>/api/posts" \
      -H "Authorization: Bearer <token>" \
      -H "Content-Type: application/json" \
      -d '{"title":"聚会","type":"娱乐","activityTime":"2026-06-01 18:00:00","needCount":3,"campus":"仙林校区"}'
    ```

- 查询帖子（分页 + 过滤）
  - GET /api/posts
  - Query params:
    - campus, type, keyword, status, grade
    - startTime, endTime (格式: yyyy-MM-dd HH:mm:ss 或 ISO)
    - pageNum (默认1), pageSize (默认10)
  - 返回 IPage<PostVO>，data.records 为数组
  - 示例：GET /api/posts?keyword=羽毛球&pageNum=1&pageSize=10

- 帖子详情
  - GET /api/posts/{id}
  - 返回 PostVO

- 更新帖子
  - PUT /api/posts/{id}
  - Header: Authorization
  - Body: PostCreateDTO
  - 成功：{ code:200 }

- 关闭帖子
  - PUT /api/posts/{id}/close
  - Header: Authorization
  - 成功：{ code:200 }

- 删除帖子
  - DELETE /api/posts/{id}
  - Header: Authorization
  - 成功：{ code:200 }


3) 评论（Comment）

- 创建评论
  - POST /api/posts/{id}/comments
  - Header: Authorization
  - Body: CommentCreateDTO { content }
  - 成功：{ code:200 }

- 获取帖子评论
  - GET /api/posts/{id}/comments
  - 返回 CommentVO 列表

- 删除评论
  - DELETE /api/comments/{id}
  - Header: Authorization


4) 收藏（Favorite）

- 添加收藏
  - POST /api/posts/{id}/favorite
  - Header: Authorization

- 取消收藏
  - DELETE /api/posts/{id}/favorite
  - Header: Authorization

- 查询某帖是否已收藏
  - GET /api/posts/{id}/favorite/status
  - 返回 { data: true/false }


5) 报名/申请（Application）

- 报名某帖
  - POST /api/posts/{id}/apply
  - Header: Authorization
  - Body: ApplicationCreateDTO { message }

- 获取某帖的申请列表
  - GET /api/posts/{id}/applications
  - Header: Authorization (帖子发布者或管理员可见)

- 审核（通过）
  - PUT /api/applications/{id}/pass
  - Header: Authorization

- 审核（驳回）
  - PUT /api/applications/{id}/reject
  - Header: Authorization

- 取消申请（申请人）
  - PUT /api/applications/{id}/cancel
  - Header: Authorization


6) 管理员相关（Admin）

- 管理员获取帖子列表
  - GET /api/admin/posts?pageNum&pageSize

- 回收站帖子
  - GET /api/admin/posts/recycle?pageNum&pageSize

- 管理员删除帖子
  - DELETE /api/admin/posts/{id}

- 管理员获取用户
  - GET /api/admin/users?pageNum&pageSize

- 禁用/解禁用户
  - PUT /api/admin/users/{id}/ban
  - PUT /api/admin/users/{id}/unban

- 恢复回收站帖子
  - PUT /api/admin/posts/{id}/restore


错误处理与校验
---------------
- 请求体校验使用 Jakarta Validation（注解如 @NotBlank, @NotNull, @Size）。如果校验失败，返回 code=400，message 包含具体字段错误信息。
- 全局异常使用 `GlobalExceptionHandler` 统一转换为 `Result`。

示例：登录并使用返回的 token 调用受保护接口
------------------------------------------------
1) 登录获取 token
```bash
curl -X POST "https://<host>/api/user/login" -H "Content-Type: application/json" -d '{"username":"u","password":"p"}'
```
响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {"token":"eyJ...","userInfo":{...}}
}
```

2) 使用 token 创建帖子
```bash
curl -X POST "https://<host>/api/posts" \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"title":"聚会","type":"娱乐","activityTime":"2026-06-01 18:00:00","needCount":3,"campus":"仙林校区"}'
```

附录：常见字段约束（简要）
- title: 必填, <=100
- type: 必填
- activityTime: 必填, LocalDateTime
- needCount: 必填, >=1
- campus: 必填
- comment.content: 必填, <=500

---
文档结束。如需我把这个文件 commit & push 到远程仓库，请回复“commit and push”并提供要使用的提交信息；或者告知要修改/补充的接口细节（例如：补充返回字段的完整示例或加入 swagger 链接）。
