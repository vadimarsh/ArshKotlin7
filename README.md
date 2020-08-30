**API:**

1. Список всех постов
GET https://arshposts.herokuapp.com/api/v1/posts
2. Один пост
GET https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
3. Добавить пост
POST https://arshposts.herokuapp.com/api/v1/posts
{
    "id":"id(default:-1)"
	"author_id": "author",
	"posttype":"posttype"
	"content": "content"
}
4. Удалить пост
DELETE https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
5. Лайк пост
POST https://arshposts.herokuapp.com/api/v1/posts/like/{post_Id}
6. Дислайк с поста
POST https://arshposts.herokuapp.com/api/v1/posts/dislike/{post_Id}
7. Расшарить пост
POST https://arshposts.herokuapp.com/api/v1/posts/share/{post_Id}