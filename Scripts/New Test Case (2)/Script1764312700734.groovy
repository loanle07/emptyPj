import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import com.kms.katalon.core.util.KeywordUtil

// 1. Định nghĩa Hash của Commit cần kiểm tra
// Thay thế bằng SHA-1 hash của commit bạn muốn verify
def commitHash = "e11c7aff24c27484c2f2ec2c8aa4b72d4728796f"


//def gitExecutablePath = "/usr/bin/git"
// 2. Định nghĩa lệnh Git Verify
//def gitCommand = "git -v ${commitHash}"

def gitCommand = ["/usr/bin/git", "verify-commit",commitHash]

// Groovy/Java Process Builder để chạy lệnh Terminal
//def process = gitCommand.execute()
def process = new ProcessBuilder(gitCommand).start()
// Chờ lệnh hoàn thành và lấy mã thoát
process.waitFor()

// Lấy kết quả đầu ra (Output)
def output = process.in.getText()

// Lấy lỗi đầu ra (Error - nơi Git thường in thông báo lỗi và thông báo xác minh)
def errorOutput = process.err.getText()

// 3. Phân tích Kết quả

KeywordUtil.logInfo("--- Git Verify Commit Output ---")
KeywordUtil.logInfo("Commit Hash: ${commitHash}")
KeywordUtil.logInfo("Standard Output:\n$output"+output)
KeywordUtil.logInfo("Error Output:\n"+errorOutput)

// --- VERIFICATION (Kiểm tra xem commit có được ký hợp lệ không) ---

def verificationSuccess = false
def successString = "Good signature from"

if (output.contains(successString)) {
	verificationSuccess = true
	KeywordUtil.markPassed("Commit $commitHash ĐÃ được xác minh GPG thành công!")
} else {
	KeywordUtil.markFailed("Commit $commitHash XÁC MINH THẤT BẠI. Kiểm tra log chi tiết.")
	
	// Ghi log chi tiết lý do thất bại (Ví dụ: "BAD signature" hoặc "No public key")
	if (errorOutput.contains("BAD signature")) {
		KeywordUtil.logWarning("Lý do: Chữ ký không hợp lệ (BAD signature).")
	} else if (errorOutput.contains("gpg: Can't check signature")) {
		KeywordUtil.logWarning("Lý do: Không tìm thấy public key của người ký.")
	}
}