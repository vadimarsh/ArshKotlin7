**API:**

Доступны без авторизации:
1. Статичный контент
GET https://arshposts.herokuapp.com/api/v1/static/{content_name}
2. Загрузить медиа на сервер
POST https://arshposts.herokuapp.com/api/v1/media
3. Регистрация нового пользователя
POST https://arshposts.herokuapp.com/api/v1/registration
4. Аутентификация пользователя
POST https://arshposts.herokuapp.com/api/v1/authentication

После аутентифакии зарегистрированным пользователям доступны:

5. Получить токен
GET https://arshposts.herokuapp.com/api/v1/me
6. Получить все посты
GET https://arshposts.herokuapp.com/api/v1/posts
7. Получить конкретный пост
GET https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
8. Добавить пост
POST https://arshposts.herokuapp.com/api/v1/posts
9. Изменить свой пост по id
POST https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
10. Удалить свой пост по id
DELETE https://arshposts.herokuapp.com/api/v1/posts/{post_Id}
11. Лайк пост по id
POST https://arshposts.herokuapp.com/api/v1/posts/like/{post_Id}
12. Дислайк с поста по id
POST https://arshposts.herokuapp.com/api/v1/posts/dislike/{post_Id}
13. Репост поста по id
POST https://arshposts.herokuapp.com/api/v1/posts/share/{post_Id}

JSON для авторизации/регистрации
{
    "username": "login_value",
    "password": "password_value"
}

JSON поста:
{   
	"id": "long_value",
    "postType": "POSTBASIC",
    "author_name": "login_value",
    "content": "content_value",
    "created": "time_value",    
    "coord": "pair",
    "videoUrl": "url",
    "repost": "post_id",
    "promoImgUrl": "url",
    "promoUrl": "url"
}
