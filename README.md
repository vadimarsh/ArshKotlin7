API:

1. Список всех постов
GET /api/v1/posts
2. Один пост
GET /api/v1/posts/{postId}
3. Добавить пост
POST /api/v1/posts*
{
    "id":"id(default:-1)"
	"author_id": "author",
	"posttype":"posttype"
	"content": "content"
}
4. Удалить пост
DELETE /api/v1/posts/{postId}
5. Лайк пост
POST /api/v1/posts/like/{postId}
6. Дислайк с поста
POST /api/v1/posts/dislike/{postId}
7. Расшарить пост
POST /api/v1/posts/share/{postId}