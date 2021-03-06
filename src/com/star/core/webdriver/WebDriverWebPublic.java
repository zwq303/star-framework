package com.star.core.webdriver;

/**  
 * @author 测试仔刘毅
 */

import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.star.core.webdriver.user.JScriptCollection;
import com.star.core.webdriver.user.RuntimeSupport;
import com.star.core.webdriver.user.WebDriverTable;
import com.star.logging.frame.LoggingManager;

public class WebDriverWebPublic extends WebDriverController {

	private static final LoggingManager LOG = new LoggingManager(WebDriverWebPublic.class.getName());
	protected static final String FORMATTER = "_yyyyMMdd_HHmmssSSS";
	protected static By tabFinder = null;
	protected static WebDriverTable webTable = null;
	protected final RuntimeSupport supprt = new RuntimeSupport(driver);

	/**
	 * wait util the element visible in max wait time setting</BR>
	 * if not visible at last, throw ElementNotVisibleException to the operations</BR>
	 * 在指定时间内循环等待，直到对象可见，超时之后直接抛出对象不可见异常信息。
	 * 
	 * @param element the WebElement to be judged
	 * @param timeout timeout setting in seconds
	 * @throws ElementNotVisibleException
	 */
	protected void waitUtilElementVisible(WebElement element, int timeout) {
		long start = System.currentTimeMillis();
		boolean isDisplayed = false;
		while (!isDisplayed && ((System.currentTimeMillis() - start) < timeout * 1000)) {
			isDisplayed = (element == null)? false : element.isDisplayed();
		}
		if (!isDisplayed){
			throw new ElementNotVisibleException("the element is not visible in " + timeout + "seconds!");
		}
	}

	/**
	 * wait util the element visible in max wait time setting</BR>
	 * if not visible at last, throw ElementNotVisibleException to the operations</BR>
	 * 在指定时间内循环等待，直到对象可见，使用用户指定的默认超时设置。
	 * 
	 * @param element the WebElement to be judged
	 * @throws ElementNotVisibleException
	 */
	protected void waitUtilElementVisible(WebElement element) {
		waitUtilElementVisible(element, maxWaitfor);
	}

	/**
	 * wait util the element visible in max wait time setting</BR>
	 * if not visible at last, throw ElementNotVisibleException to the operations</BR>
	 * 在指定时间内循环等待，直到对象可见，使用用户指定的默认超时设置。
	 * 
	 * @param by the WebElement locator
	 * @param timeout timeout setting in seconds
	 */
	protected void waitUtilElementVisible(By by, int timeout) {
		long start = System.currentTimeMillis();
		boolean isDisplayed = false;
		setElementLocateTimeout(1);
		while (!isDisplayed && ((System.currentTimeMillis() - start) < timeout * 1000)) {
			WebElement element = findElement(by);
			isDisplayed = (element == null) ? false : element.isDisplayed();
		}
		ASSERT.setExitOnAssertFailure(true);
		ASSERT.assertTrue("element " + by.toString() + " not visible in " + timeout + " seconds!", isDisplayed);
		setElementLocateTimeout(maxWaitfor);
	}

	/**
	 * wait util the element visible in max wait time setting</BR>
	 * if not visible at last, throw ElementNotVisibleException to the operations</BR>
	 * 在指定时间内循环等待，直到对象可见，使用用户指定的默认超时设置。
	 * 
	 * @param by the WebElement locator
	 */
	protected void waitUtilElementVisible(By by) {
		waitUtilElementVisible(by, maxWaitfor);
	}

	/**
	 * execute js functions to do something</BR>
	 * 使用remote webdriver执行JS函数。
	 * 
	 * @param js js function string
	 * @param report text content to be reported
	 * @param args js execute parameters
	 */
	protected void jsExecutor(String js, String report, Object args) {
		((JavascriptExecutor) driver).executeScript(js, args);
		pass(report);
	}

	/**
	 * execute js functions to do something</BR>
	 * 使用remote webdriver执行JS函数。
	 * 
	 * @param js js function string
	 * @param report text content to be reported
	 */
	protected void jsExecutor(String js, String report){
		((JavascriptExecutor)driver).executeScript(js);
		pass(report);
	}

	/**
	 * get some value from js functions.</BR>
	 * 使用remote webdriver执行JS函数并且获得返回值。
	 * 
	 * @param js js function string
	 */
	protected Object jsReturner(String js){
		return ((JavascriptExecutor)driver).executeScript(js);
	}

	/**
	 * take a screen shot and save the file by path and name</BR>
	 * 网页截图操作，按照指定的文件名称保存快照文件。
	 * 
	 * @param fileName the file path&name of the screenshot to be saved
	 */
	protected void takeScreenShot(String fileName) {
		supprt.screenShot(fileName);
	}

	/**
	 * rewrite the screenShot method, using default path and name</BR>
	 * 网页截图操作，默认路径为工程日志目录，文件名为运行的class名和时间戳拼接而成。
	 */
	protected void takeScreenShot() {
		String time = STRUTIL.formatedTime(FORMATTER);
		String fileName = LOG_ABS + this.getClass().getName() + time + ".png";
		takeScreenShot(fileName);
		pass("screenshot saved, you can see: " + fileName);
	}

	/**
	 * judge if the alert is existing</BR>
	 * 判断弹出的对话框（Dialog）是否存在。
	 */
	protected boolean alertExists() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ne) {
			return false;
		}
	}

	/**
	 * judge if the alert is present in specified seconds</BR>
	 * 在指定的时间内判断弹出的对话框（Dialog）是否存在。
	 * 
	 * @param seconds timeout in seconds
	 */
	protected boolean alertExists(int seconds) {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) < seconds * 1000) {
			try {
				driver.switchTo().alert();
				return true;
			} catch (NoAlertPresentException ne) {
			}
		}
		return false;
	}

	/**
	 * judge if the element is existing</BR>
	 * 判断指定的对象是否存在。
	 * 
	 * @param by the element locator By
	 */
	protected boolean elementExists(By by) {
		return (driver.findElements(by).size() > 0) ? true : false;
	}

	/**
	 * judge if the element is present in specified seconds</BR>
	 * 在指定的时间内判断指定的对象是否存在。
	 * 
	 * @param by the element locator By
	 * @param seconds timeout in seconds
	 */
	protected boolean elementExists(final By by, int seconds) {
		long start = System.currentTimeMillis();
		boolean exists = false;
		setElementLocateTimeout(1);
		while (!exists && ((System.currentTimeMillis() - start) < seconds * 1000)) {
			exists = driver.findElements(by).size() > 0;
		}
		setElementLocateTimeout(maxWaitfor);
		return exists;
	}

	/**
	 * judge if the browser is existing, using part of the page title</BR>
	 * 按照网页标题判断页面是否存在，标题可使用部分内容匹配。
	 * 
	 * @param browserTitle part of the title to see if browser exists
	 */
	protected boolean browserExists(String browserTitle) {
		try {
			String defaultHandle = driver.getWindowHandle();
			Set<String> windowHandles = driver.getWindowHandles();
			for (int i = 0; i < 20; i++) {
				pause(500);
				if (driver.getWindowHandles().equals(windowHandles)) {
					break;
				}
				if (i == 20 && !driver.getWindowHandles().equals(windowHandles)) {
					return false;
				}
			}
			for (String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				if (driver.getTitle().contains(browserTitle)) {
					driver.switchTo().window(defaultHandle);
					return true;
				}
			}
			driver.switchTo().window(defaultHandle);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * refresh current browser page by url re-navigate</BR>
	 * 通过当前页面URL跳转的方式重新加载当前页面。
	 */
	protected void browserRefresh(){
		driver.navigate().to(driver.getCurrentUrl());
		tableRefresh();
	}

	/**
	 * judge if the browser is present by title reg pattern in specified seconds</BR>
	 * 在指定时间内按照网页标题判断页面是否存在，标题可使用部分内容匹配。
	 * 
	 * @param browserTitle part of the title to see if browser exists
	 * @param seconds timeout in seconds
	 */
	protected boolean browserExists(String browserTitle, int seconds) {
		long start = System.currentTimeMillis();
		boolean isExist = false;
		while (!isExist && (System.currentTimeMillis() - start) < seconds * 1000) {
			isExist = browserExists(browserTitle);
		}
		return isExist;
	}

	/**
	 * maximize browser window: support ie, ff3.6 and lower</BR>
	 * 网页窗口最大化操作。
	 */
	protected void maximizeWindow() {
		windowMaximize();
		//jsExecutor(JScriptCollection.MAXIMIZE_WINDOW.getValue(), "current window maximized");
	}

	/**
	 * maximize browser window</BR>
	 * 网页窗口最大化操作。
	 */
	protected void windowMaximize() {
		driver.manage().window().maximize();
	}

	/**
	 * select default window and default frame</BR>
	 * 在当前页面中自动选择默认的页面框架（frame）。
	 */
	protected void selectDefaultWindowFrame() {
		driver.switchTo().defaultContent();
		pass("switch to default frame on window");
	}

	/**
	 * switch to active element</BR>
	 * 在当前操作的页面和对象时自动选择已被激活的对象。
	 */
	protected void focusOnActiveElement() {
		driver.switchTo().activeElement();
		pass("switch to active element");
	}

	/**
	 * switch to new window supporting, by deleting first hanlder</BR>
	 * 选择最新弹出的窗口，需要预存第一个窗口的handle。
	 * 
	 * @param firstHandle the first window handle
	 */
	protected void selectNewWindow(String firstHandle) {
		Set<String> handles = null;
		Iterator<String> it = null;
		handles = driver.getWindowHandles();
		handles = driver.getWindowHandles();
		handles.remove(firstHandle);
		it = handles.iterator();
		while (it.hasNext()) {
			driver.switchTo().window(it.next());
		}
		driver.switchTo().defaultContent();
		pass("switch to new window");
	}

	/**
	 * switch to new window supporting, by deleting original hanlde</BR>
	 * 选择最新弹出的窗口，需要预存所有已有窗口的handles。
	 * 
	 * @param originalHandles the old window handles
	 */
	protected void selectNewWindow(Set<String> originalHandles) {
		Set<String> newHandles = driver.getWindowHandles();
		newHandles = driver.getWindowHandles();
		Iterator<String> olds = originalHandles.iterator();
		while(olds.hasNext()){
			newHandles.remove(olds.next());			
		}
		Iterator<String> news = newHandles.iterator();
		while (news.hasNext()) {
			driver.switchTo().window(news.next());
		}
		driver.switchTo().defaultContent();
		pass("switch to new window");
	}

	/**
	 * switch to window by title</BR>
	 * 按照网页标题选择窗口，标题内容需要全部匹配。
	 * 
	 * @param windowTitle the title of the window to be switched to
	 */
	protected void selectWindow(String windowTitle) {
		Set<String> windowHandles = null;
		windowHandles = driver.getWindowHandles();
		windowHandles = driver.getWindowHandles();
		for (String handle : windowHandles) {
			driver.switchTo().window(handle);
			String title = driver.getTitle();
			if (windowTitle.equals(title)) {
				pass("switch to window [ " + windowTitle + " ]");
				driver.switchTo().defaultContent();
				return;
			}
		}
		LOG.error("there is no window named [ " + windowTitle + " ]");
		failAndExit("there is no window named [ " + windowTitle + " ]");
	}

	/**
	 * switch to window by title</BR>
	 * 按照网页标题选择窗口，标题内容需要全部匹配，超时未出现则报错。
	 * 
	 * @param windowTitle the title of the window to be switched to.
	 * @param timeout time to wait for the window appears, unit of seconds.
	 */
	protected void selectWindowWithTimeout(String windowTitle, int timeout) {
		ASSERT.assertTrue("window is not present after " + timeout + "seconds!", 
				browserExists(windowTitle, timeout));
		selectWindow(windowTitle);
	}
	
	/**
	 * switch to parent window when child was closed unexpectly.</BR>
	 * 在打开的子窗口被意外（被动、非工具预期的行为）关闭之后，切换回父窗口。
	 *
	 * @param handles handles set when child windows are still alive.
	 * @param childHandle child window whitch to be closed.
	 * @param parentHandle the parent handle of windows.
	 */
	protected void selectParentWindow(Set<String> handles, String childHandle, String parentHandle){
		if (!handles.toString().contains(childHandle) || !handles.toString().contains(parentHandle)){
			throw new IllegalArgumentException("you are using the wrong parameters!");
		}
		handles.remove(childHandle);
		driver.switchTo().window(parentHandle);
		waitForAlertDisappear(5);
	}

	/**
	 * close window by window title and its index if has the same title, by string full pattern</BR>
	 * 按照网页标题选择并且关闭窗口，重名窗口按照指定的重名的序号关闭，标题内容需要全部匹配。
	 * 
	 * @param windowTitle the title of the window to be closed.
	 * @param index the index of the window which shared the same title, begins with 1.
	 */
	protected void closeWindow(String windowTitle, int index) {
		Object[] winArray = null;
		List<String> winList = new ArrayList<String>();
		winArray = driver.getWindowHandles().toArray();
		winArray = driver.getWindowHandles().toArray();
		for (int i = 0; i < winArray.length - 1; i++) {
			driver.switchTo().window(winArray[i].toString());
			if (windowTitle.equals(driver.getTitle())) {
				winList.add(winArray[i].toString());
			}
		}
		driver.switchTo().window(winList.get(index - 1));
		driver.switchTo().defaultContent();
		driver.close();
		pass("window [ " + windowTitle + " ] closed by index [" + index + "]");
	}

	/**
	 * close the last window by the same window title, by string full pattern</BR>
	 * 按照网页标题选择窗口，适用于无重名的窗口，标题内容需要全部匹配。
	 * 
	 * @param windowTitle the title of the window to be closed.
	 */
	protected void closeWindow(String windowTitle) {
		Object[] winArray = driver.getWindowHandles().toArray();
		winArray = driver.getWindowHandles().toArray();
		for (int i = winArray.length - 1; i > 0; i--) {
			driver.switchTo().window(winArray[i].toString());
			if (windowTitle.equals(driver.getTitle())) {
				driver.switchTo().defaultContent();
				driver.close();
				break;
			}
		}
		pass("window [ " + windowTitle + " ] closed ");
	}

	/**
	 * close windows except specified window title, by string full pattern</BR>
	 * 关闭除了指定标题页面之外的所有窗口，适用于例外窗口无重名的情况，标题内容需要全部匹配。
	 * 
	 * @param windowTitle the title of the window not to be closed
	 */
	protected void closeWindowExcept(String windowTitle) {
		Set<String> windowHandles = driver.getWindowHandles();
		windowHandles = driver.getWindowHandles();
		for (String handle : windowHandles) {
			driver.switchTo().window(handle);
			String title = driver.getTitle();
			if (!windowTitle.equals(title)) {
				driver.switchTo().defaultContent();
				driver.close();
			}
		}
		pass("all windows closed except [ " + windowTitle + " ]");
	}

	/**
	 * close windows except specified window title, by string full pattern</BR>
	 * 关闭除了指定标题页面之外的所有窗口，例外窗口如果重名，按照指定的重名顺序关闭，标题内容需要全部匹配。
	 * 
	 * @param windowTitle the title of the window not to be closed
	 * @param index the index of the window to keep shared the same title with others, begins with 1.
	 */
	protected void closeWindowExcept(String windowTitle, int index) {
		Set<String> windowHandles = driver.getWindowHandles();
		windowHandles = driver.getWindowHandles();
		for (String handle : windowHandles) {
			driver.switchTo().window(handle);
			String title = driver.getTitle();
			if (!windowTitle.equals(title)) {
				driver.switchTo().defaultContent();
				driver.close();
			}
		}

		Object[] winArray = driver.getWindowHandles().toArray();
		winArray = driver.getWindowHandles().toArray();
		for (int i = 0; i < winArray.length; i++) {
			driver.switchTo().window(winArray[i].toString());
			if (i + 1 != index) {
				driver.switchTo().defaultContent();
				driver.close();
			}
		}
		pass("keep only window [ " + windowTitle + " ] by title index [ " + index + " ]");
	}

	/**
	 * wait for new window which has no title in few seconds</BR>
	 * 判断在指定的时间内是否有新的窗口弹出，无论其是否有标题。
	 * 
	 * @param browserCount windows count before new window appears.
	 * @param seconds time unit in seconds.
	 */
	protected boolean isNewWindowExits(int browserCount, int seconds) {
		Set<String> windowHandles = null;
		boolean isExist = false;
		long begins = System.currentTimeMillis();
		while ((System.currentTimeMillis() - begins < seconds * 1000) && !isExist) {
			windowHandles = driver.getWindowHandles();
			windowHandles = driver.getWindowHandles();
			isExist = (windowHandles.size() > browserCount) ? true : false;
		}
		return isExist;
	}

	/**
	 * wait for new window which has no title in few seconds</BR>
	 * 判断在指定的时间内是否有新的窗口弹出，无论其是否有标题。
	 * 
	 * @param oldHandles windows handle Set before new window appears.
	 * @param seconds time unit in seconds.
	 */
	protected boolean isNewWindowExits(Set<String> oldHandles, int seconds) {
		boolean isExist = false;
		Set<String> windowHandles = null;
		long begins = System.currentTimeMillis();
		while ((System.currentTimeMillis() - begins < seconds * 1000) && !isExist) {
			windowHandles = driver.getWindowHandles();
			windowHandles = driver.getWindowHandles();
			isExist = (windowHandles.size() > oldHandles.size()) ? true : false;
		}
		return isExist;
	}

	/**
	 * select a frame by index</BR>
	 * 按照序号选择框架（frame）。
	 * 
	 * @param index the index of the frame to select
	 */
	protected void selectFrame(int index) {
		driver.switchTo().frame(index);
		pass("select frame by index [ " + index + " ]");
	}

	/**
	 * select a frame by name or id</BR>
	 * 按照名称或者ID选择框架（frame）。
	 * 
	 * @param nameOrId the name or id of the frame to select
	 */
	protected void selectFrame(String nameOrId) {
		driver.switchTo().frame(nameOrId);
		pass("select frame by name or id [ " + nameOrId + " ]");
	}

	/**
	 * select a frame by frameElement</BR>
	 * 按照框架对象本身选择框架（frame）。
	 * 
	 * @param frameElement the frame element to select
	 */
	protected void selectFrame(WebElement frameElement) {
		driver.switchTo().frame(frameElement);
		pass("select frame by frameElement");
	}

	/**
	 * select a frame by frame element locator: By</BR>
	 * 按照指定的元素定位方式选择框架（frame）。
	 * 
	 * @param by the frame element locator
	 */
	protected void selectFrame(By by) {
		driver.switchTo().frame(driver.findElement(by));
		pass("select frame by frame locator [ " + by.toString() + " ]");
	}

	/**
	 * select a frame by name or id, throw exception when timeout.</BR>
	 * 按照名称或者ID选择框架（frame），在指定时间内frame不存在则报错。
	 * 
	 * @param nameOrId the name or id of the frame to select.
	 * @param timeout time to wait for the frame available, unit of seconds.
	 */
	protected void selectFrameWithTimeout(String nameOrId, int timeout) {
		waitForAndSwitchToFrame(nameOrId, timeout);
		pass("select frame by name or id [ " + nameOrId + " ]");
	}

	/**
	 * select a frame by frame element locator: By.</BR>
	 * 按照指定的元素定位方式选择框架（frame），在指定时间内frame不存在则报错。
	 * 
	 * @param by the frame element locator.
	 * @param timeout time to wait for the frame available, unit of seconds.
	 */
	protected void selectFrameWithTimeout(By by, int timeout) {
		waitForElementPresent(by, timeout);
		driver.switchTo().frame(driver.findElement(by));
		pass("select frame by frame locator [ " + by.toString() + " ]");
	}

	/**
	 * edit a content editable iframe</BR>
	 * 编辑指定框架（frame）内的最直接展示文本内容。
	 * 
	 * @param by the frame element locaotr
	 * @param text the text string to be input
	 */
	protected void editFrameText(By by, String text) {
		driver.switchTo().frame(driver.findElement(by));
		driver.switchTo().activeElement().sendKeys(text);
		pass("input text [ " + text + " ] to frame [ " + by.toString() + " ]");
	}

	/**
	 * rewrite the get method, adding user defined log</BR>
	 * 地址跳转方法，使用WebDriver原生get方法，加入失败重试的次数定义。
	 * 
	 * @param url the url you want to open.
	 * @param actionCount retry times when load timeout occuers.
	 */
	protected void get(String url, int actionCount) {
		for (int i = 0; i < actionCount; i ++){
			if (i == 0){
				setPageLoadTimeout(30);
			}
			try {
				driver.get(url);
				pass("navigate to url [ " + url + " ]");
				return;
			} catch (TimeoutException e) {
			} finally{
				setPageLoadTimeout(maxLoadTime);				
			}
		}
	}

	/**
	 * rewrite the get method, adding user defined log</BR>
	 * 地址跳转方法，使用WebDriver原生get方法，默认加载超重试【1】次。
	 * 
	 * @param url the url you want to open.
	 */
	protected void get(String url) {
		get(url, 2);
	}

	/**
	 * navigate to some where by url</BR>
	 * 地址跳转方法，与WebDriver原生navigate.to方法内容完全一致。
	 * 
	 * @param url the url you want to open
	 */
	protected void navigateTo(String url){
		driver.navigate().to(url);
		pass("navigate to url [ " + url + " ]");
	}

	/**
	 * navigate back</BR>
	 * 地址跳转方法，与WebDriver原生navigate.back方法内容完全一致。
	 * 
	 * @throws RuntimeException
	 */
	protected void navigateBack(){
		driver.navigate().back();
		pass("navigate back");
	}

	/**
	 * navigate forward</BR>
	 * 地址跳转方法，与WebDriver原生navigate.forward方法内容完全一致。
	 */
	protected void navigateForward(){
		driver.navigate().forward();
		pass("navigate forward");
	}

	/**
	 * rewrite the click method, adding user defined log</BR>
	 * 在等到对象可见之后点击指定的对象。
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void click(WebElement element) {
		waitUtilElementVisible(element);
		element.click();
		pass("click on WebElement");
	}

	/**
	 * rewrite the click method, click on the element to be find by By</BR>
	 * 在等到对象可见之后点击指定的对象。
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void click(By by) {
		waitUtilElementVisible(driver.findElement(by));
		driver.findElement(by).click();
		pass("click on element [ " + by.toString() + " ] ");
	}

	/**
	 * forcely click, by executing javascript</BR>
	 * 在等到对象可见之后点击指定的对象，使用JavaScript执行的方式去操作，</BR>
	 * 这种方法使用过后一般需要调用一次selectDefaultWindowFrame以确保运行稳定。
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void clickByJavaScript(WebElement element) {
		waitUtilElementVisible(element);
		jsExecutor(JScriptCollection.CLICK_BY_JAVASCRIPT.getValue(), "click on element", element);
	}

	/**
	 * forcely click, by executing javascript</BR>
	 * 在等到对象可见之后点击指定的对象，使用JavaScript执行的方式去操作，</BR>
	 * 这种方法使用过后一般需要调用一次selectDefaultWindowFrame以确保运行稳定。
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void clickByJavaScript(By by) {
		waitUtilElementVisible(driver.findElement(by));
		jsExecutor(JScriptCollection.CLICK_BY_JAVASCRIPT.getValue(), 
				"click on element [ " + by.toString() + " ] ", driver.findElement(by));
	}

	/**
	 * doubleclick on the element to be find by By</BR>
	 * 在等到对象可见之后双击指定的对象.
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void doubleClick(WebElement element) {
		waitUtilElementVisible(element);
		actionDriver.doubleClick(element);
		actionDriver.perform();
		pass("doubleClick on element ");
	}

	/**
	 * doubleclick on the element</BR>
	 * 在等到对象可见之后双击指定的对象.
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void doubleClick(By by) {
		waitUtilElementVisible(driver.findElement(by));
		actionDriver.doubleClick(findElement(by));
		actionDriver.perform();
		pass("doubleClick on element [ " + by.toString() + " ] ");
	}

	/**
	 * right click on the element to be find by By</BR>
	 * 在等到对象可见之后鼠标右键点击指定的对象.
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void rightClick(WebElement element) {
		waitUtilElementVisible(element);
		actionDriver.contextClick(element);
		actionDriver.perform();
		pass("rightClick on element ");
	}

	/**
	 * right click on the element</BR>
	 * 在等到对象可见之后鼠标右键点击指定的对象。
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void rightClick(By by) {
		waitUtilElementVisible(driver.findElement(by));
		actionDriver.contextClick(findElement(by));
		actionDriver.perform();
		pass("rightClick on element [ " + by.toString() + " ] ");
	}

	/**
	 * rewrite the submit method, adding user defined log</BR>
	 * 在等到指定对象可见之后在该对象上做确认/提交的操作。
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void submit(WebElement element) {
		waitUtilElementVisible(element);
		element.submit();
		pass("submit on element");
	}

	/**
	 * rewrite the submit method, submit on the element to be find by By</BR>
	 * 在等到指定对象可见之后在该对象上做确认/提交的操作。
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void submit(By by) {
		waitUtilElementVisible(driver.findElement(by));
		driver.findElement(by).submit();
		pass("submit on element [ " + by.toString() + " ]");
	}

	/**
	 * rewrite the clear method, adding user defined log</BR>
	 * 在等到指定对象可见之后在该对象上做清理操作，一般用于输入框和选择框。
	 * 
	 * @param element the webelement you want to operate
	 */
	protected void clear(WebElement element) {
		waitUtilElementVisible(element);
		element.clear();
		pass("element cleared");
	}

	/**
	 * rewrite the clear method, clear on the element to be find by By</BR>
	 * 在等到指定对象可见之后在该对象上做清理操作，一般用于输入框和选择框。
	 * 
	 * @param by the locator you want to find the element
	 */
	protected void clear(By by) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		element.clear();
		pass("element [ " + by.toString() + " ] cleared");
	}

	/**
	 * rewrite the sendKeys method, adding user defined log</BR>
	 * 以追加文本的模式在指定可编辑对象中输入文本，操作之前自动等待到对象可见。
	 * 
	 * @param element the webelement you want to operate
	 * @param text the text you want to input to element
	 */
	protected void sendKeysAppend(WebElement element, String text) {
		waitUtilElementVisible(element);
		element.sendKeys(text);
		pass("send text [ " + text + " ] to element");
	}

	/**
	 * rewrite the sendKeys method, sendKeys on the element to be find by By</BR>
	 * 以追加文本的模式在指定可编辑对象中输入文本，操作之前自动等待到对象可见。
	 * 
	 * @param by the locator you want to find the element
	 * @param text the text you want to input to element
	 */
	protected void sendKeysAppend(By by, String text) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		element.sendKeys(text);
		pass("input text [ " + text + " ] to element [ " + by.toString() + " ]");
	}

	/**
	 * rewrite the sendKeys method, adding user defined log</BR>
	 * 清理指定对象中已经输入的内容重新输入，操作之前自动等待到对象可见。
	 * 
	 * @param element the webelement you want to operate
	 * @param text the text you want to input to element
	 */
	protected void sendKeys(WebElement element, String text) {
		waitUtilElementVisible(element);
		element.clear();
		element.sendKeys(text);
		pass("send text [ " + text + " ] to WebEdit");
	}

	/**
	 * rewrite the sendKeys method, sendKeys on the element to be find by By</BR>
	 * 清理指定对象中已经输入的内容重新输入，操作之前自动等待到对象可见。
	 * 
	 * @param by the locator you want to find the element
	 * @param text the text you want to input to element
	 */
	protected void sendKeys(By by, String text) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		element.clear();
		element.sendKeys(text);
		pass("input text [ " + text + " ] to element [ " + by.toString() + " ]");
	}

	/**
	 * readonly text box or richtext box input</BR>
	 * 使用DOM（Documnet Object Modal）修改页面中对象的文本属性值，使用ID定位对象则返回唯一对象，其余返回数组。
	 * 
	 * @param by the attribute of the element, default support is TagName/Name/Id
	 * @param byValue the attribute value of the element
	 * @param text the text you want to input to element
	 * @param index the index of the elements shared the same attribute value
	 * @throws IllegalArgumentException
	 */
	protected void sendKeysByDOM(String by, String byValue, String text, int index) {
		String js = null;

		if (by.equalsIgnoreCase("tagname")) {
			js = "document.getElementsByTagName('" + byValue + "')[" + index + "].value='" + text + "'";
		} else if (by.equalsIgnoreCase("name")) {
			js = "document.getElementsByName('" + byValue + "')[" + index + "].value='" + text + "'";
		} else if (by.equalsIgnoreCase("id")) {
			js = "document.getElementById('" + byValue + "').value='" + text + "'";
		} else {
			throw new IllegalArgumentException("only can find element by TagName/Name/Id");
		}

		jsExecutor(js, "input text [ " + text + " ] to element [ " + by + " ]");
	}

	/**
	 * readonly text box or richtext box input, finding elements by element id</BR>
	 * 按照ID定位页面中对象，并使用DOM（Documnet Object Modal）修改其文本属性值。
	 * 
	 * @param elementId the id of the element
	 * @param text the text you want to input to element
	 */
	protected void sendKeysById(String elementId, String text) {
		sendKeysByDOM("Id", elementId, text, 0);
	}

	/**
	 * readonly text box or richtext box input, finding elements by element name</BR>
	 * 按照名称（Name）和序号定位页面中对象，并使用DOM（Documnet Object Modal）修改其文本属性值。
	 * 
	 * @param elementName the name of the element
	 * @param text the text you want to input to element
	 * @param elementIndex the index of the elements shared the same name, begins with 0
	 */
	protected void sendKeysByName(String elementName, String text, int elementIndex) {
		sendKeysByDOM("Name", elementName, text, elementIndex);
	}

	/**
	 * readonly text box or richtext box input, finding elements by element tag name</BR>
	 * 按照标签名称（TagName）和序号定位页面中对象，并使用DOM（Documnet Object Modal）修改其文本属性值。
	 * 
	 * @param elementTagName the tag name of the element
	 * @param text the text you want to input to element
	 * @param elementIndex the index of the elements shared the same tag name, begins with 0
	 */
	protected void sendKeysByTagName(String elementTagName, String text, int elementIndex) {
		sendKeysByDOM("TagName", elementTagName, text, elementIndex);
	}

	/**
	 * sendKeys by using keybord event on element</BR>
	 * 使用键盘模拟的方法在指定的对象上输入指定的文本。
	 * 
	 * @param element the webelement you want to operate
	 * @param text the text you want to input to element
	 */
	protected void sendKeysByKeybord(WebElement element, String text) {
		waitUtilElementVisible(element);
		actionDriver.sendKeys(element, text);
		actionDriver.perform();
		pass("send text [ " + text + " ] to WebEdit");
	}

	/**
	 * sendKeys by using keybord event on element to be found by By</BR>
	 * 使用键盘模拟的方法在指定的对象上输入指定的文本。
	 * 
	 * @param by the locator you want to find the element
	 * @param text the text you want to input to element
	 */
	protected void sendKeysByKeybord(By by, String text) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		actionDriver.sendKeys(element, text);
		actionDriver.perform();
		pass("input text [ " + text + " ] to element [ " + by.toString() + " ]");
	}

	/**
	 * edit rich text box created by kindeditor</BR>
	 * 使用JS调用KindEditor对象本身的接口，在页面KindEditor对象中输入指定的文本。
	 * 
	 * @param editorId kindeditor id
	 * @param text the text you want to input to element
	 */
	protected void sendKeysOnKindEditor(String editorId, String text) {
		String javascript = "KE.html('" + editorId + "','<p>" + text + "</p>');";
		jsExecutor(javascript, "input text [ " + text + " ] to kindeditor");
	}

	/**
	 * select an item from a picklist by index</BR>
	 * 按照指定序号选择下拉列表中的选项。
	 * 
	 * @param element the picklist element
	 * @param index the index of the item to be selected
	 */
	protected void selectByIndex(WebElement element, int index) {
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByIndex(index);
		pass("item selected by index [ " + index + " ]");
	}

	/**
	 * select an item from a picklist by index</BR>
	 * 按照指定序号选择下拉列表中的选项。
	 * 
	 * @param by the locator you want to find the element
	 * @param index the index of the item to be selected
	 */
	protected void selectByIndex(By by, int index) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByIndex(index);
		pass("item selected by index [ " + index + " ] on [ " + by.toString() + " ]");
	}

	/**
	 * select an item from a picklist by item value</BR>
	 * 按照指定选项的实际值（不是可见文本值，而是对象的“value”属性的值）选择下拉列表中的选项。
	 * 
	 * @param element the picklist element
	 * @param itemValue the item value of the item to be selected
	 */
	protected void selectByValue(WebElement element, String itemValue) {
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByValue(itemValue);
		pass("item selected by item value [ " + itemValue + " ]");
	}

	/**
	 * select an item from a picklist by item value</BR>
	 * 按照指定选项的实际值（不是可见文本值，而是对象的“value”属性的值）选择下拉列表中的选项。
	 * 
	 * @param by the locator you want to find the element
	 * @param itemValue the item value of the item to be selected
	 */
	protected void selectByValue(By by, String itemValue) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByValue(itemValue);
		pass("item selected by item value [ " + itemValue + " ] on [ " + by.toString() + " ]");
	}

	/**
	 * select an item from a picklist by item value</BR>
	 * 按照指定选项的可见文本值（用户直接可以看到的文本）选择下拉列表中的选项。
	 * 
	 * @param element the picklist element
	 * @param text the item value of the item to be selected
	 */
	protected void selectByVisibleText(WebElement element, String text) {
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByVisibleText(text);
		pass("item selected by visible text [ " + text + " ]");
	}

	/**
	 * select an item from a picklist by item value</BR>
	 * 按照指定选项的可见文本值（用户直接可以看到的文本）选择下拉列表中的选项。
	 * 
	 * @param by the locator you want to find the element
	 * @param text the item value of the item to be selected
	 */
	protected void selectByVisibleText(By by, String text) {
		WebElement element = driver.findElement(by);
		waitUtilElementVisible(element);
		Select select = new Select(element);
		select.selectByVisibleText(text);
		pass("item selected by visible text [ " + text + " ] on [ " + by.toString() + " ]");
	}

	/**
	 * set the checkbox on or off</BR>
	 * 将指定的复选框对象设置为选中或者不选中状态。
	 * 
	 * @param element the checkbox element
	 * @param onOrOff on or off to set the checkbox
	 */
	protected void setCheckBox(WebElement element, String onOrOff) {
		WebElement checkElement = element.findElement(By.tagName("input"));
		waitUtilElementVisible(checkElement);
		if ((onOrOff.toLowerCase().contains("on") && !checkElement.isSelected())
				|| (onOrOff.toLowerCase().contains("off") && checkElement.isSelected())) {
			element.click();
		}
		pass("the checkbox is set to [ " + onOrOff.toUpperCase() + " ]");
	}

	/**
	 * set the checkbox on or off</BR>
	 * 将指定的复选框对象设置为选中或者不选中状态。
	 * 
	 * @param by the locator you want to find the element
	 * @param onOrOff on or off to set the checkbox
	 */
	protected void setCheckBox(By by, String onOrOff) {
		WebElement checkBox = driver.findElement(by);
		waitUtilElementVisible(checkBox);
		WebElement checkElement = checkBox.findElement(By.tagName("input"));
		if ((onOrOff.toLowerCase().contains("on") && !checkElement.isSelected())
				|| (onOrOff.toLowerCase().contains("off") && checkElement.isSelected())) {
			checkBox.click();
		}
		pass("the checkbox [ " + by.toString() + " ] is set to [ " + onOrOff.toUpperCase() + " ]");
	}

	/**
	 * find elements displayed on the page</BR>
	 * 按照指定的定位方式寻找所有可见的对象。
	 * 
	 * @param by the way to locate webelements
	 * @return displayed webelement list
	 */
	protected List<WebElement> findDisplayedElments(By by) {
		List<WebElement> elementList = new ArrayList<WebElement>();
		WebElement element;
		List<WebElement> elements = driver.findElements(by);
		Iterator<WebElement> it = elements.iterator();
		while ((element = it.next()) != null && element.isDisplayed()) {
			elementList.add(element);
		}
		int eleNum = elementList.size();
		if (eleNum > 0) {
			pass("got" + eleNum + "displayed elements [ " + by.toString() + " ]");
		} else {
			warn("there is not displayed element found by [" + by.toString() + " ]");
		}
		return elementList;
	}

	/**
	 * find elements displayed on the page</BR>
	 * 按照指定的定位方式寻找第一可见的对象。
	 * 
	 * @param by the way to locate webelement
	 * @return the first displayed webelement
	 */
	protected WebElement findDisplayedElment(By by) {
		List<WebElement> elements = findDisplayedElments(by);
		return (elements.size() > 0) ? elements.get(0) : null;
	}

	/**
	 * rewrite the findElements method, adding user defined log</BR>
	 * 按照指定的定位方式寻找象。
	 * 
	 * @param by the locator of the elements to be find
	 * @return the webelements you want to find
	 */
	protected List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	/**
	 * rewrite the findElement method, adding user defined log</BR>
	 * 按照指定的定位方式寻找象。
	 * 
	 * @param by the locator of the element to be find
	 * @return the first element accord your locator
	 */
	protected WebElement findElement(By by) {
		List<WebElement> elements = findElements(by);
		return (elements.size() > 0) ? (elements.get(0)) : null;
	}

	/**
	 * store the WebDriverWebTable object, it only changes on By changing</BR>
	 * 缓存WebTable对象，在WebTable对象不为空的情况下（为空则直接新建对象），</BR>
	 * 如果定位方式相同则直接返回原有对象，否则重新创建WebTable对象。
	 * 
	 * @param tabBy the element locator By
	 */
	private WebDriverTable tableCache(By tabBy) {
		waitUtilElementVisible(tabBy);
		if (tabFinder == null) {
			tabFinder = tabBy;
			return new WebDriverTable(driver, tabBy);
		} else {
			if (tabBy.toString().equals(tabFinder.toString())) {
				return webTable;
			} else {
				tabFinder = tabBy;
				return new WebDriverTable(driver, tabBy);
			}
		}
	}

	/**
	 * refresh the webtable on the same locator, only if it changes</BR>
	 * 如果同一定位方式的WebTable内容发生变化需要重新定位，则需要刷新WebTable。
	 */
	protected void tableRefresh(){
		WebDriverWebPublic.tabFinder = null;
		WebDriverWebPublic.webTable = null;		
	}

	/**
	 * get row count of a webtable</BR>
	 * 返回一个WebTable的行的总数。
	 * 
	 * @param tabBy By, by which you can locate the webTable
	 * @return the row count of the webTable
	 */
	protected int tableRowCount(By tabBy) {
		webTable = tableCache(tabBy);
		int rowCount = webTable.rowCount();
		pass("the webTable " + tabBy.toString() + "has row count: [ " + rowCount + " ]");
		return rowCount;
	}

	/**
	 * get column count of a specified webtable row</BR>
	 * 返回一个WebTable在制定行上的列的总数。
	 * 
	 * @param tabBy By, by which you can locate the webTable
	 * @param rowNum row index of your webTable to count
	 * @return the column count of the row in webTable
	 */
	protected int tableColCount(By tabBy, int rowNum) {
		webTable = tableCache(tabBy);
		int colCount = webTable.colCount(rowNum);
		pass("count columns of the webTable " + tabBy.toString() 
			+ " on the row [ " + rowNum + " ], got: [ " + colCount + " ]");
		return colCount;
	}

	/**
	 * get the element in the webTable cell by row and col index</BR>
	 * 返回WebTable中指定行、列和类型的子元素，如按钮、链接、输入框等。
	 * 
	 * @param tabBy By, by which you can locate the webTable
	 * @param row row index of the webTable.
	 * @param col column index of the webTable.
	 * @param type the element type, such as "img"/"a"/"input" or "image/link/button/webedit"
	 * @param index element index in the specified cell, begins with 1.
	 * @return the webTable cell WebElement
	 */
	protected WebElement tableChildElement(By tabBy, int row, int col, String type, int index) {
		return tableCache(tabBy).childItem(row, col, type, index);
	}

	/**
	 * get the cell text of the webTable on specified row and column</BR>
	 * 返回WebTable的指定行和列的中的文本内容。
	 * 
	 * @param tabBy By, by which you can locate the webTable
	 * @param row row index of the webTable.
	 * @param col column index of the webTable.
	 * @return the cell text
	 */
	protected String tableCellText(By tabBy, int row, int col) {
		webTable = tableCache(tabBy);
		String text = webTable.cellText(row, col);
		pass("the text of cell[" + row + "," + col + "] is: [ " + text + " ]");
		return text;
	}

	/**
	 * wait for window appears in the time unit seconds</BR>
	 * 在指定时间内等待窗口出现，超时则报错，用以缓冲运行，增加健壮性。
	 * 
	 * @param browserTitle the title of the browser window.
	 * @param seconds timeout in timeunit of seconds.
	 * @return if the window exists.
	 */
	protected boolean waitForWindowPresent(String browserTitle, int seconds) {
		ASSERT.assertTrue("window is not present after " + seconds + "seconds!", browserExists(browserTitle, seconds));
		return true;
	}

	/**
	 * wait for window appears in the time unit seconds</BR>
	 * 在指定时间内等待新窗口出现，超时则报错，用以缓冲运行，增加健壮性。
	 */
	protected boolean waitForNewWindowOpened(int oldCount, int seconds) {
		ASSERT.assertTrue("new window did not opened in " + seconds + "seconds!",isNewWindowExits(oldCount, seconds));
		return true;
	}

	/**
	 * wait for window appears in the time unit seconds</BR>
	 * 在指定时间内等待新窗口出现，超时则报错，用以缓冲运行，增加健壮性。
	 */
	protected boolean waitForNewWindowOpened(Set<String> oldHandles, int seconds) {
		ASSERT.assertTrue("new window did not opened in " + seconds + "seconds!",
				isNewWindowExits(oldHandles, seconds));
		return true;
	}

	/**
	 * wait for and switch to frame when avilable in timeout setting</BR>
	 * 在指定时间内等待，直到指定框架出现并且选择他。
	 * 
	 * @param locator the id or name of frames.
	 * @param seconds timeout in seconds
	 */
	protected boolean waitForAndSwitchToFrame(String locator, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * use js to judge if the browser load completed.
	 * 用js返回值判断页面是否加载完毕。
	 *
	 * @return load comploete or not.
	 */
	protected boolean pageLoadSucceed() {
		Object loadCompleted = jsReturner(JScriptCollection.BROWSER_READY_STATUS.getValue());
		return loadCompleted.toString().toLowerCase().equals("complete");
	}

	/**
	 * use js to judge if the browser load completed.
	 * 用js返回值判断页面是否加载完毕，超时未加载完毕则报错。
	 *
	 * @param timeout max time used to load page.
	 */
	protected boolean waitForPageToLoad(int timeout){
		long start = System.currentTimeMillis();
		boolean loadCompleted = false;
		while (!loadCompleted && ((System.currentTimeMillis() - start) < timeout * 1000)) {
			loadCompleted = pageLoadSucceed();
			pause(stepTimeUnit);
		}
		ASSERT.assertTrue("the page did not load complete in " + timeout + "seconds!",loadCompleted);
		return true;
	}

	/**
	 * wait for alert appears in the time unit of seconds</BR>
	 * 在指定时间内等待，对话框（Dialog）出现，用以缓冲运行，增加健壮性。
	 */
	protected boolean waitForAlertPresent(int seconds) {
		ASSERT.assertTrue("alert does not appear in " + seconds + " seconds!", alertExists(seconds));
		return true;
	}

	/**
	 * wait for alert disappears in the time unit of seconds</BR>
	 * 在指定时间内等待，对话框（Dialog）消失，用以缓冲运行，增加健壮性。
	 */
	protected boolean waitForAlertDisappear(int seconds) {
		long start = System.currentTimeMillis();
		boolean exists = true;
		while ((System.currentTimeMillis() - start) < seconds * 1000) {
			try {
				driver.switchTo().alert();
			} catch (NoAlertPresentException ne) {
				exists = false;
				break;
			}
		}
		ASSERT.assertFalse("alert does not disappear in " + seconds + " seconds!", exists);
		return exists;
	}


	/**
	 * wait for the element visiable in timeout setting</BR>
	 * 在指定时间内等待，直到对象可见。
	 * 
	 * @param by the element locator By
	 * @param seconds timeout in seconds
	 */
	protected boolean waitForElementVisible(By by, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.visibilityOfElementLocated(by)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for the element visiable in timeout setting</BR>
	 * 在指定时间内等待，直到对象可见。
	 * 
	 * @param element the element to be found.
	 * @param seconds timeout in seconds.
	 */
	protected boolean waitForElementVisible(WebElement element, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.visibilityOf(element)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for the element not visiable in timeout setting</BR>
	 * 在指定时间内等待，直到对象不可见。
	 * 
	 * @param by the element locator.
	 * @param seconds timeout in seconds.
	 */
	protected boolean waitForElementNotVisible(By by, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.invisibilityOfElementLocated(by)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for the element present in timeout setting</BR>
	 * 在指定时间内等待，直到对象出现在页面上。
	 * 
	 * @param by the element locator.
	 * @param seconds timeout in seconds.
	 */
	protected boolean waitForElementPresent(By by, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.presenceOfElementLocated(by)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for the element clickable in timeout setting</BR>
	 * 在指定时间内等待，直到对象能够被点击。
	 * 
	 * @param by the element locator By
	 * @param seconds timeout in seconds
	 */
	protected boolean waitForElementClickable(By by, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.elementToBeClickable(by)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for text appears on element in timeout setting</BR>
	 * 在指定时间内等待，直到指定对象上出现指定文本。
	 * 
	 * @param by the element locator By
	 * @param text the text to be found of element
	 * @param seconds timeout in seconds
	 */
	protected boolean waitForTextOnElement(By by, String text, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.textToBePresentInElement(by, text)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * wait for text appears in element attributes in timeout setting</BR>
	 * 在指定时间内等待，直到指定对象的某个属性值等于指定文本。
	 * 
	 * @param by the element locator By
	 * @param text the text to be found in element attributes
	 * @param seconds timeout in seconds
	 */
	protected boolean waitForTextOfElementAttr(By by, String text, int seconds) {
		try {
			setElementLocateTimeout(seconds);
			WebDriverWait wait = new WebDriverWait(driver, seconds, stepTimeUnit);
			return wait.until(ExpectedConditions.textToBePresentInElementValue(by, text)) != null;
		} finally {
			setElementLocateTimeout(maxWaitfor);
		}
	}

	/**
	 * make the alert dialog not to appears</BR>
	 * 通过JS函数重载，在对话框（Alert）出现之前点击掉它，或者说等价于不让其出现。
	 */
	protected void ensrueBeforeAlert() {
		jsExecutor(JScriptCollection.ENSRUE_BEFORE_ALERT.getValue(),
				"rewrite js to ensure alert before it appears");
	}

	/**
	 * make the warn dialog not to appears when window.close()</BR>
	 * 通过JS函数重载，在浏览器窗口关闭之前除去它的告警提示。
	 */
	protected void ensureBeforeWinClose() {
		jsExecutor(JScriptCollection.ENSURE_BEFORE_WINCLOSE.getValue(),
				"rewrite js to ensure window close event");
	}

	/**
	 * make the confirm dialog not to appears choose default option OK</BR>
	 * 通过JS函数重载，在确认框（Confirm）出现之前点击确认，或者说等价于不让其出现而直接确认。
	 */
	protected void ensureBeforeConfirm() {
		jsExecutor(JScriptCollection.ENSURE_BEFORE_CONFIRM.getValue(),
				"rewrite js to ensure confirm before it appears");
	}

	/**
	 * make the confirm dialog not to appears choose default option Cancel</BR>
	 * 通过JS函数重载，在确认框（Confirm）出现之前点击取消，或者说等价于不让其出现而直接取消。
	 */
	protected void dismissBeforeConfirm() {
		jsExecutor(JScriptCollection.DISMISS_BEFORE_CONFIRM.getValue(),
				"rewrite js to dismiss confirm before it appears");
	}

	/**
	 * make the prompt dialog not to appears choose default option OK</BR>
	 * 通过JS函数重载，在提示框（Prompt）出现之前点击确认，或者说等价于不让其出现而直接确认。
	 */
	protected void ensureBeforePrompt() {
		jsExecutor(JScriptCollection.ENSURE_BEFORE_PROMPT.getValue(),
				"rewrite js to ensure prompt before it appears");
	}

	/**
	 * make the prompt dialog not to appears choose default option Cancel</BR>
	 * 通过JS函数重载，在提示框（Prompt）出现之前点击取消，或者说等价于不让其出现而直接取消。
	 */
	protected void dismisBeforePrompt() {
		jsExecutor(JScriptCollection.DISMISS_BEFORE_PROMPT.getValue(),
				"rewrite js to dismiss prompt before it appears");
	}

	/**
	 * choose OK/Cancel button's OK on alerts</BR>
	 * 在弹出的对话框（Dialog）上点击确认/是等接受性按钮。
	 */
	protected void chooseOKOnAlert() {
		driver.switchTo().alert().accept();
		pass("click OK button on alert");
	}

	/**
	 * choose OK/Cancel button's OK on alerts within timeout setting.</BR>
	 * 在弹出的对话框（Dialog）上点击确认/是等接受性按钮，预先判断是否存在，超时不存在则报错。
	 */
	protected void chooseOKOnAlert(int timeout) {
		waitForAlertPresent(timeout);
		driver.switchTo().alert().accept();
		pass("click OK button on alert");
	}

	/**
	 * choose Cancel on alerts</BR>
	 * 在弹出的对话框（Dialog）上点击取消/否等拒绝性按钮。
	 */
	protected void chooseCancelOnAlert() {
		driver.switchTo().alert().dismiss();
		pass("click Cancel on alert dialog");
	}

	/**
	 * choose Cancel on alerts within timeout setting.</BR>
	 * 在弹出的对话框（Dialog）上点击取消/否等拒绝性按钮，预先判断是否存在，超时不存在则报错。
	 */
	protected void chooseCancelOnAlert(int timeout) {
		waitForAlertPresent(timeout);
		driver.switchTo().alert().dismiss();
		pass("click Cancel on alert dialog");
	}

	/**
	 * get the text of the alerts</BR>
	 * 返回对话框（Dialog）上的提示信息文本内容。
	 * 
	 * @return alert text string
	 */
	protected String getTextOfAlert() {
		String alerts = driver.switchTo().alert().getText();
		pass("the text of the alert is: " + alerts);
		return alerts;
	}

	/**
	 * set text on alerts</BR>
	 * 向对话框（InputBox）中输入文本。
	 * 
	 * @param text the text string you want to input on alerts
	 */
	protected void setTextOnAlert(String text) {
		driver.switchTo().alert().sendKeys(text);
		pass("set text [ " + text + " ] on alert");
	}

	/**
	 * use js to make the element to be un-hidden</BR>
	 * 使用JS执行的方法强制让某些隐藏的控件显示出来。
	 * 
	 * @param element the element to be operate
	 */
	protected void makeElementUnHidden(WebElement element) {
		jsExecutor(JScriptCollection.MAKE_ELEMENT_UNHIDDEN.getValue(), 
				"rewrite js to make elements to be visible", element);
	}

	/**
	 * use js to make the element to be un-hidden</BR>
	 * 使用JS执行的方法强制让某些隐藏的控件显示出来。
	 * 
	 * @param by the By locator to find the element
	 */
	protected void makeElementUnHidden(By by) {
		jsExecutor(JScriptCollection.MAKE_ELEMENT_UNHIDDEN.getValue(), 
				"rewrite js to make elements to be visible", driver.findElement(by));
	}

	/**
	 * rewrite the getTitle method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return the title on your current session
	 */
	protected String getWindowTitle() {
		String title = driver.getTitle();
		pass("current window title is :" + title);
		return title;
	}

	/**
	 * rewrite the getCurrentUrl method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return the url on your current session
	 */
	protected String getCurrentUrl() {
		String url = driver.getCurrentUrl();
		pass("current session url is :" + url);
		return url;
	}

	/**
	 * rewrite the getWindowHandles method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return the window handles set
	 */
	protected Set<String> getWindowHandles() {
		Set<String> handle = driver.getWindowHandles();
		handle = driver.getWindowHandles();
		pass("window handles are: " + handle.toString());
		return handle;
	}

	/**
	 * rewrite the getWindowHandle method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return the window handle string
	 */
	protected String getWindowHandle() {
		String handle = driver.getWindowHandle();
		pass("current window handle is:" + handle);
		return handle;
	}

	/**
	 * rewrite the getPageSource method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return the page source string
	 */
	protected String getPageSource() {
		String source = driver.getPageSource();
		pass("page source begins with: " + source.substring(0, 50));
		return source;
	}

	/**
	 * rewrite the getSessionId method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @return current session id string
	 */
	protected String getSessionId() {
		String sessionId = ((RemoteWebDriver)driver).getSessionId().toString();
		pass("current sessionid is:" + sessionId);
		return sessionId;
	}

	/**
	 * rewrite the getTagName method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @return the tagname string
	 */
	protected String getTagName(WebElement element) {
		String tagName = element.getTagName();
		pass("element's TagName is: " + tagName);
		return tagName;
	}

	/**
	 * rewrite the getTagName method, find the element by By and get its tag name</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @return the tagname string
	 */
	protected String getTagName(By by) {
		String tagName = driver.findElement(by).getTagName();
		pass("element [ " + by.toString() + " ]'s TagName is: " + tagName);
		return tagName;
	}

	/**
	 * rewrite the getAttribute method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @param attributeName the name of the attribute you want to get
	 * @return the attribute value string
	 */
	protected String getAttribute(WebElement element, String attributeName) {
		String value = element.getAttribute(attributeName);
		pass("element's " + attributeName + "is: " + value);
		return value;
	}

	/**
	 * rewrite the getAttribute method, find the element by By and get its attribute value</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @param attributeName the name of the attribute you want to get
	 * @return the attribute value string
	 */
	protected String getAttribute(By by, String attributeName) {
		String value = driver.findElement(by).getAttribute(attributeName);
		pass("element [ " + by.toString() + " ]'s " + attributeName + "is: " + value);
		return value;
	}

	/**
	 * rewrite the isSelected method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @return the bool value of whether is the WebElement selected
	 */
	protected boolean isSelected(WebElement element) {
		boolean isSelected = element.isSelected();
		pass("element selected? " + String.valueOf(isSelected));
		return isSelected;
	}

	/**
	 * rewrite the isSelected method, the element to be find by By</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @return the bool value of whether is the WebElement selected
	 */
	protected boolean isSelected(By by) {
		boolean isSelected = driver.findElement(by).isSelected();
		pass("element [ " + by.toString() + " ] selected? "	+ String.valueOf(isSelected));
		return isSelected;
	}

	/**
	 * rewrite the isEnabled method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @return the bool value of whether is the WebElement enabled
	 */
	protected boolean isEnabled(WebElement element) {
		boolean isEnabled = element.isEnabled();
		pass("element enabled? " + String.valueOf(isEnabled));
		return isEnabled;
	}

	/**
	 * rewrite the isEnabled method, the element to be find by By</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @return the bool value of whether is the WebElement enabled
	 */
	protected boolean isEnabled(By by) {
		boolean isEnabled = driver.findElement(by).isEnabled();
		pass("element [ " + by.toString() + " ] enabled? " + String.valueOf(isEnabled));
		return isEnabled;
	}

	/**
	 * rewrite the getText method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 */
	protected String getText(WebElement element) {
		String text = element.getText();
		pass("element text is:" + text);
		return text;
	}

	/**
	 * rewrite the getText method, find the element by By and get its own text</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @return the text string
	 */
	protected String getText(By by) {
		String text = driver.findElement(by).getText();
		pass("element [ " + by.toString() + " ]'s text is: " + text);
		return text;
	}

	/**
	 * rewrite the isDisplayed method, adding user defined log</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @return the bool value of whether is the WebElement displayed
	 */
	protected boolean isDisplayed(WebElement element) {
		boolean isDisplayed = element.isDisplayed();
		pass("element displayed? " + String.valueOf(isDisplayed));
		return isDisplayed;
	}

	/**
	 * rewrite the isDisplayed method, the element to be find by By</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @return the bool value of whether is the WebElement displayed
	 */
	protected boolean isDisplayed(By by) {
		boolean isDisplayed = driver.findElement(by).isDisplayed();
		pass("element [ " + by.toString() + " ] displayed? " + String.valueOf(isDisplayed));
		return isDisplayed;
	}

	/**
	 * get its css property value</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param element the webelement you want to operate
	 * @param propertyName the name of the property you want to get
	 * @return the css property value string
	 */
	protected String getCssValue(WebElement element, String propertyName) {
		String cssValue = element.getCssValue(propertyName);
		pass("element's css [" + propertyName + "] value is:" + cssValue);
		return cssValue;
	}

	/**
	 * find the element by By and get its css property value</BR>
	 * 与工具原生API作用完全一致，只是增加了操作结果检查和日志记录。
	 * 
	 * @param by the locator you want to find the element
	 * @param propertyName the name of the property you want to get
	 * @return the css property value string
	 */
	protected String getCssValue(By by, String propertyName) {
		String cssValue = driver.findElement(by).getCssValue(propertyName);
		pass("element [ " + by.toString() + " ]'s css[" + propertyName + "] value is: " + cssValue);
		return cssValue;
	}
}
