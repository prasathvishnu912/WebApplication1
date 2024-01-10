const appCont = document.getElementById("app");
const formEle = document.getElementById("loginForm");
const loginFormCont = document.getElementById("loginCont");
const loginFailedCont = document.getElementById("loginFailedCont");
const throttlingCont = document.getElementById("throttlingCont");

const throtExitBtn = document.getElementById("exit");
const tryAgainBtn = document.getElementById("tryAgain");

tryAgainBtn.addEventListener("click", () => {
	loginFailedCont.style.display = "none";
	loginFormCont.style.display = "block";
});

function loginProcess(data) {
	loginFormCont.style.display = "none";
	if (data === false) {
		loginFailedCont.style.display = "block";
	}
	else {
		listBooks();
	}
};

function displayBooks(book) {
	
	const authorName = book.bookName;
	const bookName = book.authorName;
	const imageUrl = book.imageUrl;

	console.log(authorName);
	console.log(bookName);
	console.log(imageUrl);

	const bookCard = document.createElement("div");
	bookCard.classList.add("book-card");

	const bookImage = document.createElement("img");
	bookImage.src = imageUrl;
	bookImage.alt = "Book Cover";
	bookImage.classList.add("book-cover");

	const bookDetails = document.createElement("div");
	bookDetails.classList.add("book-details");

	const title = document.createElement("h2");
	title.classList.add("book-title");
	title.textContent = bookName;

	const author = document.createElement("p");
	author.classList.add("book-author");
	author.textContent = `Author: ${authorName}`;

	bookDetails.appendChild(title);
	bookDetails.appendChild(author);

	bookCard.appendChild(bookImage);
	bookCard.appendChild(bookDetails);

	
	appCont.appendChild(bookCard);

};


async function listBooks() {

	const username = localStorage.getItem("username");

	try {
		const response = await fetch('/WebProject1/Login', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': username
			}
		});

		if (!response.ok) {
			throw new Error('Network response was not ok.');
		}

		const data = await response.json();
		console.log(data);
		data.forEach((value, key) => {
			displayBooks(data[key]);
		})

	} catch (error) {
		console.error('Error:', error);
	}
}

formEle.addEventListener("submit", async event => {
	event.preventDefault();

	try {
		const formData = new FormData(event.target);
		const jsonObject = {};

		formData.forEach((value, key) => {
			localStorage.setItem(key, value);
			jsonObject[key] = value;
		});

		const jsonData = JSON.stringify(jsonObject);

		console.log('JSON Data:', jsonData);

		const response = await fetch('/WebProject1/Login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: jsonData
		});

		if (!response.ok&&response.status===429) {
			throw new Error("Throttled: Too many login attempts. Try again later.");
		}

		const data = await response.json();
		loginProcess(data);
		console.log('Received Data:', data);
	} catch (error) {
		loginFormCont.style.display="none";
		throttlingCont.style.display="block";
		console.error('Error:', error);
	}
});










