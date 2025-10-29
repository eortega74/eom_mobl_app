package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Component
public class Selenium implements CommandLineRunner {

    @Override
    public void run(String... args) throws IOException {
        File file = new File("/app/reports/testcases.txt");
        System.out.println("file testcases.txt created at: " + file.getAbsolutePath() + "canWrite? " + file.canWrite());
        FileWriter fw = new FileWriter("/app/reports/testcases.txt");
        try {
            // System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            String seleniumHost = System.getenv("SELENIUM_HOST");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--ignore-certificate-errors", "--allow-insecure-localhost", "--allow-insecure-keycloak.local");
            // options.setBinary("/usr/local/bin/chrome");
            // options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
            // options.addArguments("--ignore-certificate-errors"); // ignores SSL errors
            // options.addArguments("--allow-insecure-localhost");
            
            WebDriver driver = new RemoteWebDriver(new URL("http://" + seleniumHost + ":4444/wd/hub"), options);
            System.out.println("connected to remote driver!!!");
            //driver.get("http://ate-java17.app.ray.com");
            driver.get("https://keycloak.local:8080/login");
            System.out.println("able to fetch dev from internet!");

            WebElement duendeLink = driver.findElement(By.xpath("/html/body/div/div/a"));
            duendeLink.click();

            WebElement usernameInput = driver.findElement(By.xpath("//*[@id='Input_Username']"));
            usernameInput.clear();
            String username = "editplus";
            usernameInput.sendKeys(username);

            WebElement passwordInput = driver.findElement(By.xpath("//*[@id='Input_Password']"));
            passwordInput.clear();
            String password = "editplus";
            passwordInput.sendKeys(password);

            WebElement submitButton = driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/div/div[2]/form/button[1]"));
            submitButton.click();

            System.out.println("****** Congrats! You are logged in! ******");
            fw.write("***Test case 1 passed, you can log in***\n");
            System.out.println("file test case 1 documented");



           
            WebElement manageUsersButton = driver.findElement(By.xpath("/html/body/div/button"));
            manageUsersButton.click();

            WebElement createUserButton = driver.findElement(By.xpath("/html/body/div[1]/button"));
            createUserButton.click();
            WebElement createNameInput = driver.findElement(By.xpath("//*[@id='createForm']/div[1]/input"));
            createNameInput.clear();
            createNameInput.sendKeys("Test User");
            WebElement createEmailInput = driver.findElement(By.xpath("//*[@id='createForm']/div[2]/input"));
            createEmailInput.clear();
            createEmailInput.sendKeys("Test.Users@rtx.com");
            WebElement ssoIDInput = driver.findElement(By.xpath("//*[@id='createForm']/div[4]/input"));
            ssoIDInput.clear();
            ssoIDInput.sendKeys("E21019029");
            WebElement createUserSubmitButton = driver.findElement(By.xpath("/html/body/form/div[5]/button"));
            createUserSubmitButton.click();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.urlContains("/admin"));


            System.out.println("current url after creation: " + driver.getCurrentUrl());
            if(driver.getCurrentUrl().equals("https://keycloak.local:8080/admin")) {
                System.out.println("Test case 2 passed");
                fw.write("***Test case 2 passed, can't add user with duplicate IDs***\n");
            } else {
                System.out.println("Test case 2 failed");
                fw.write("***Test case 2 failed, added user with duplicate ID***\n");
                driver.navigate().back();
            }
            WebElement createUserButtonTwo = driver.findElement(By.xpath("/html/body/div[1]/button"));
            createUserButtonTwo.click();
            WebElement createNameInputTwo = driver.findElement(By.xpath("//*[@id='createForm']/div[1]/input"));
            createNameInputTwo.clear();
            createNameInputTwo.sendKeys("Test User");
            WebElement createEmailInputTwo = driver.findElement(By.xpath("//*[@id='createForm']/div[2]/input"));
            createEmailInputTwo.clear();
            createEmailInputTwo.sendKeys("Test.Users@rtx.com");
            WebElement ssoIDInputTwo = driver.findElement(By.xpath("//*[@id='createForm']/div[4]/input"));
            ssoIDInputTwo.clear();
            ssoIDInputTwo.sendKeys("E11111113");
            WebElement createUserSubmitButtonTwo = driver.findElement(By.xpath("/html/body/form/div[5]/button"));
            createUserSubmitButtonTwo.click();
            wait.until(ExpectedConditions.urlContains("/create"));
            if(driver.getCurrentUrl().equals("https://keycloak.local:8080/admin")) {
                System.out.println("Test case 3 failed");
                fw.write("***Test case 3 failed, unable to create new user with new ID***\n");
                driver.navigate().back();
            } else {
                System.out.println("Test case 3 passed");
                fw.write("***Test case 3 passed, able to create user with new ID***\n");
                driver.navigate().to("https://keycloak.local:8080/admin");
            }
            System.out.println("driver url checkpoint 2: " + driver.getCurrentUrl());

            driver.navigate().to("https://keycloak.local:8080/edit/E11111113");
            WebElement editNameInput = driver.findElement(By.xpath("/html/body/form/div[1]/input"));
            editNameInput.clear();
            editNameInput.sendKeys("User Test");
            WebElement editEmailInput = driver.findElement(By.xpath("/html/body/form/div[2]/input"));
            editEmailInput.clear();
            editEmailInput.sendKeys("User.Test@rtx.com");
            WebElement editRole = driver.findElement(By.xpath("/html/body/form/div[3]/select"));
            Select select = new Select(editRole);
            select.selectByValue("admin");
            WebElement editSubmitButton = driver.findElement(By.xpath("/html/body/form/div[5]/button"));
            editSubmitButton.click();
            wait.until(ExpectedConditions.urlContains("/admin"));
            if(driver.getCurrentUrl().equals("https://keycloak.local:8080/admin")) {
                System.out.println("Test case 4 passed");
                fw.write("***Test case 4 passed, able to edit user***\n");
            } else {
                System.out.println("Test case 4 failed");
                fw.write("***Test case 4 failed, unable to edit user***\n");
            }
            driver.navigate().to("https://keycloak.local:8080/delete/E21019029");
            System.out.println("navigating to: " + driver.getCurrentUrl());
            WebElement deleteButton = driver.findElement(By.xpath("/html/body/div[1]/button"));
            deleteButton.click();
            WebElement continueDeleteButton = driver.findElement(By.xpath("//*[@id='continueButton']"));
            continueDeleteButton.click();
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert text: " + alert.getText());
            alert.accept();
            wait.until(ExpectedConditions.urlContains("/delete")); //admin
            if(driver.getCurrentUrl().contains("delete")) {
                System.out.println("Test case 5 passed");
                fw.write("***Test case 5 passed, unable to delete yourself***\n");
                driver.navigate().to("https://keycloak.local:8080/delete/E11111113");
            } else {
                System.out.println("Test case 5 failed");
                fw.write("***Test case 5 failed, able to delete yourself***\n");
                driver.navigate().to("https://keycloak.local:8080/delete/E11111113");
            }
            WebElement deleteButtonTwo = driver.findElement(By.xpath("/html/body/div[1]/button"));
            deleteButtonTwo.click();
            System.out.println("Current url is in second delete test: " + driver.getCurrentUrl());
            WebElement continueDeleteButtonTwo = driver.findElement(By.xpath("//*[@id='continueButton']"));
            continueDeleteButtonTwo.click();
            wait.until(ExpectedConditions.urlContains("/admin")); //delete
            if(driver.getCurrentUrl().equals("https://keycloak.local:8080/admin")) {
                System.out.println("Test case 6 passed");
                fw.write("***Test case 6 passed, able to delete user created in this session***\n");
            } else {
                System.out.println("Test case 6 failed");
                fw.write("***Test case 6 failed, unable to delete user created in this session***\n");
                driver.navigate().back();
            }

            //WebElement deleteUserButton = driver.findElement(By.xpath("/html/body/div[2]/table/tbody/tr[1]/td[5]/span/button[3]"));
            // deleteUserButton.click();
            driver.navigate().to("https://keycloak.local:8080/home");
            System.out.println("current url trying to logout is: " + driver.getCurrentUrl());
            WebElement logoutButton = driver.findElement(By.xpath("/html/body/footer/form/button"));
            logoutButton.click();
            fw.write("***Test case 7 passed, able to logout***\n");
            System.out.println("***** You have logged out, test complete! *******");
            //create a testing report pdf or txt file to report results
            //Ex test case 1 - Logging in, passed or failed
            driver.quit();
            fw.close();
       } catch(Exception e) {
            System.out.println("Exception e: " + e);
            fw.close();
       }
    }
}
