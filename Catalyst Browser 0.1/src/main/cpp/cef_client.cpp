#include "cef_client.h"
#include "cef_wrapper.h"
#include <include/wrapper/cef_helpers.h>

CefClient::CefClient(CefWrapper* wrapper) : wrapper_(wrapper) {}

CefRefPtr<CefRenderHandler> CefClient::GetRenderHandler() override {
    return wrapper_;
}

// CefLifeSpanHandler methods
void CefClient::OnAfterCreated(CefRefPtr<CefBrowser> browser) {
    CEF_REQUIRE_UI_THREAD();
    // Nothing to do here as the browser is stored in CefWrapper
}

bool CefClient::DoClose(CefRefPtr<CefBrowser> browser) {
    CEF_REQUIRE_UI_THREAD();
    // Allow the close to proceed
    return false;
}

void CefClient::OnBeforeClose(CefRefPtr<CefBrowser> browser) {
    CEF_REQUIRE_UI_THREAD();
    // Nothing to do here
}

// CefLoadHandler methods
void CefClient::OnLoadStart(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, TransitionType transition_type) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

void CefClient::OnLoadEnd(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, int httpStatusCode) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

void CefClient::OnLoadError(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, ErrorCode errorCode, const CefString& errorText, const CefString& failedUrl) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

// CefDisplayHandler methods
void CefClient::OnTitleChange(CefRefPtr<CefBrowser> browser, const CefString& title) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

void CefClient::OnAddressChange(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, const CefString& url) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

void CefClient::OnStatusMessage(CefRefPtr<CefBrowser> browser, const CefString& value) {
    CEF_REQUIRE_UI_THREAD();
    // Could notify Java via JNI here
}

// CefRequestHandler methods
bool CefClient::OnBeforeBrowse(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, CefRefPtr<CefRequest> request, bool user_gesture, bool is_redirect) {
    CEF_REQUIRE_UI_THREAD();
    // Return false to allow the navigation to proceed
    return false;
}

// CefContextMenuHandler methods
void CefClient::OnBeforeContextMenu(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, CefRefPtr<CefContextMenuParams> params, CefRefPtr<CefMenuModel> model) {
    CEF_REQUIRE_UI_THREAD();
    // Clear the menu to disable the context menu
    model->Clear();
}

// CefKeyboardHandler methods
bool CefClient::OnKeyEvent(CefRefPtr<CefBrowser> browser, const CefKeyEvent& event, CefEventHandle os_event) {
    CEF_REQUIRE_UI_THREAD();
    // Return false to allow the default handling
    return false;
}