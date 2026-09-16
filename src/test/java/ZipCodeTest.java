import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipCodeTest {

    private static final String REGISTER_URL = "https://sharelane.com/cgi-bin/register.py";
    private static final String MAIN_URL = "https://sharelane.com/cgi-bin/main.py";
    private static final String ADD_TO_CART_URL = "https://sharelane.com/cgi-bin/add_to_cart.py?book_id=4";
    private static final String SHOPPING_CART_URL = "https://sharelane.com/cgi-bin/shopping_cart.py";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void checkZipCode4digits() {

        // Регистрация
        driver.get(REGISTER_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("zip_code")));
        driver.findElement(By.name("zip_code")).sendKeys("123456");
        driver.findElement(By.cssSelector("[value='Continue']")).click();

        System.out.println("Zip-code пройден");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("first_name")));

        driver.findElement(By.name("first_name")).sendKeys("Daniel");
        driver.findElement(By.name("last_name")).sendKeys("Izteleuev");
        driver.findElement(By.name("email")).sendKeys("test@mail.com");
        driver.findElement(By.name("password1")).sendKeys("TestPassword");
        driver.findElement(By.name("password2")).sendKeys("TestPassword");
        driver.findElement(By.cssSelector("[value='Register']")).click();

        System.out.println("Регистрация пройдена");

        // Получение email
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String pageText = driver.findElement(By.tagName("body")).getText();

        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.sharelane\\.com");
        Matcher matcher = pattern.matcher(pageText);

        if (!matcher.find()) {
            throw new AssertionError("Email с доменом .sharelane.com не найден на странице");
        }

        String email = matcher.group();

        System.out.println("Найденный email: " + email);

        // Авторизация
        driver.get(MAIN_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys("1111");
        driver.findElement(By.cssSelector("[value='Login']")).click();

        System.out.println("Авторизация пройдена");

        // Поиск книги
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("keyword")));

        driver.findElement(By.name("keyword")).sendKeys("War and Peace");
        driver.findElement(By.cssSelector("[value='Search']")).click();

        System.out.println("Поиск выполнен");

        // Добавление в корзину
        driver.get(ADD_TO_CART_URL);

        System.out.println("Книга добавлена в корзину");

        // Корзина
        driver.get(SHOPPING_CART_URL);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[value='Proceed to Checkout']")));
        driver.findElement(By.cssSelector("[value='Proceed to Checkout']")).click();

        System.out.println("Переход к оформлению заказа");

        // Оплата
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("card_number")));
        driver.findElement(By.name("card_number")).sendKeys("4242 4242 4242 4242");
        driver.findElement(By.cssSelector("[value='Make Payment']")).click();

        System.out.println("Оплата выполнена, но не  выполненна так как у меня нет карты visa/mastercard и т.д.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}