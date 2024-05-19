package messages

type SimpleResponse struct {
	Message string `json:"message"`
}

type LoginResponse struct {
	Id       int32  `json:"id"`
	Username string `json:"username"`
	Token    string `json:"token"`
}
