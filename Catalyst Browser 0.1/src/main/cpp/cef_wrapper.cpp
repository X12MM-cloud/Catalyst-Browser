#include "cef_wrapper.h"
#include <include/cef_app.h>
#include <include/cef_browser.h>
#include <include/cef_client.h>
#include <include/cef_render_handler.h>
#include <include/base/cef_bind.h>
#include <include/wrapper/cef_helpers.h>

// Initialize static instance
CefWrapper* CefWrapper::instance_ = nullptr;

CefWrapper* CefWrapper::GetInstance() {
    if (!instance_) {
        instance_ = new CefWrapper();
    }
    return instance_;
}

CefWrapper::CefWrapper() 
    : browser_(nullptr),
      view_width_(1024),
      view_height_(768) {
}

CefWrapper::~CefWrapper() {
    Shutdown();
}

bool CefWrapper::Initialize() {
    CefSettings settings;
    settings.windowless_rendering_enabled = true;
    settings.no_sandbox = true;
    
    // Initialize CEF
    CefInitialize(CefMainArgs(), settings, this, nullptr);
    
    // Start the CEF message loop
    CefRunMessageLoop();
    
    return true;
}

void CefWrapper::Shutdown() {
    if (browser_) {
        browser_->GetHost()->CloseBrowser(true);
        browser_ = nullptr;
    }
    
    CefShutdown();
}

void CefWrapper::OnContextInitialized() {
    CEF_REQUIRE_UI_THREAD();
    
    // Default URL if none is provided
    CreateBrowser("about:blank");
}

bool CefWrapper::CreateBrowser(const std::string& url) {
    CEF_REQUIRE_UI_THREAD();
    
    CefWindowInfo window_info;
    window_info.SetAsWindowless(0);  // No parent window
    
    CefBrowserSettings browser_settings;
    browser_settings.windowless_frame_rate = 60;  // 60 fps
    
    // Create the browser window
    browser_ = CefBrowserHost::CreateBrowserSync(
        window_info, 
        static_cast<CefClient*>(this), 
        url, 
        browser_settings, 
        nullptr, 
        nullptr);
    
    return browser_ != nullptr;
}

void CefWrapper::LoadURL(const std::string& url) {
    CEF_REQUIRE_UI_THREAD();
    
    if (browser_) {
        browser_->GetMainFrame()->LoadURL(url);
    }
}

void CefWrapper::ExecuteJavaScript(const std::string& code) {
    CEF_REQUIRE_UI_THREAD();
    
    if (browser_) {
        browser_->GetMainFrame()->ExecuteJavaScript(
            code, browser_->GetMainFrame()->GetURL(), 0);
    }
}

void CefWrapper::GetViewRect(CefRefPtr<CefBrowser> browser, CefRect& rect) {
    rect.x = 0;
    rect.y = 0;
    rect.width = view_width_;
    rect.height = view_height_;
}

void CefWrapper::OnPaint(CefRefPtr<CefBrowser> browser,
                        PaintElementType type,
                        const RectList& dirtyRects,
                        const void* buffer,
                        int width, int height) {
    // Lock to prevent concurrent access to the pixel buffer
    std::lock_guard<std::mutex> lock(buffer_mutex_);
    
    // Update dimensions
    view_width_ = width;
    view_height_ = height;
    
    // Calculate buffer size (BGRA - 4 bytes per pixel)
    const size_t buffer_size = width * height * 4;
    
    // Resize our buffer if needed
    pixel_buffer_.resize(buffer_size);
    
    // Copy the pixel data
    memcpy(pixel_buffer_.data(), buffer, buffer_size);
}

bool CefWrapper::GetLatestBuffer(std::vector<uint8_t>& buffer, int& width, int& height) {
    std::lock_guard<std::mutex> lock(buffer_mutex_);
    
    if (pixel_buffer_.empty()) {
        return false;
    }
    
    buffer = pixel_buffer_;
    width = view_width_;
    height = view_height_;
    
    return true;
}