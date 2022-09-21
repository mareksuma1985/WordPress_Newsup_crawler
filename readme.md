# WordPress Newsup crawler
A [Selenium](https://www.selenium.dev/) automatic test that visits every article on a site that uses WordPress with [Newsup](https://www.themessearch.com/wordpressthemes/newsup/) theme. It can be adapted to work with other themes by modifying XPath expressions. You need Java, IntelliJ IDEA, Chrome browser and [ChromeDriver](https://chromedriver.chromium.org/) in `c:\Program Files\chromedriver` folder to run it. You can choose if links leading to external sites should be visited too by changing the value of `openExternal` variable in [config.properties](src/main/resources/config.properties) file. Put the name of the site you want to crawl as value of `baseURL` variable. You can generate a `.jar` file by clicking `Build` / `Build Artifacts...` in IntelliJ IDEA. See also: [How to build jars from IntelliJ properly?](https://stackoverflow.com/questions/1082580/how-to-build-jars-from-intellij-properly). The [WordPress_Newsup_crawler.jar](WordPress_Newsup_crawler.jar) file will read the two parameters from properties file.

[![newsup.jpg](screenshots/newsup.jpg)](https://www.themessearch.com/wordpressthemes/newsup/)