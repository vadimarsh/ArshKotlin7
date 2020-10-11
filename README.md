**API:**

Доступны без авторизации:
1. Регистрация нового пользователя
POST https://arshposts.herokuapp.com/api/v1/registration
2. Аутентификация пользователя
POST https://arshposts.herokuapp.com/api/v1/authentication

После аутентифакии зарегистрированным пользователям доступны:

3. Получить токен
GET https://arshposts.herokuapp.com/api/v1/me
4. Получить все посты
GET https://arshposts.herokuapp.com/api/v1/posts
5. Получить конкретный пост
GET https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
6. Добавить пост
POST https://arshposts.herokuapp.com/api/v1/posts
7. Изменить свой пост по id
POST https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
8. Удалить свой пост по id
DELETE https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
9. Лайк пост по id
POST https://arshposts.herokuapp.com/api/v1/posts/like/{post_Id}
10. Дислайк с поста по id
POST https://arshposts.herokuapp.com/api/v1/posts/dislike/{post_Id}
11. Репост поста по id
POST https://arshposts.herokuapp.com/api/v1/posts/share/{post_Id}
12. Статичный контент
GET https://arshposts.herokuapp.com/api/v1/static/{content_name}
13. Загрузить медиа на сервер
POST https://arshposts.herokuapp.com/api/v1/media

JSON для авторизации/регистрации
{
    "username": "login_value",
    "password": "password_value"
}

JSON поста:
{   
	"id": "long_value",
    "sourceId": "long_value",
	"content": "string_value",
    "link": "url",
    "attachmentId": "string_value"
}
