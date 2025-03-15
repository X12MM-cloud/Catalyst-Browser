#pragma once

#include <include/cef_app.h>
#include <include/cef_client.h>
#include <include/cef_render_handler.h>
#include <memory>
#include <vector>
#include <mutex>

class CefWrapper : public CefApp,
                   public CefBrowserProcessHandler,
                   public CefRenderHandler {
public:
    static CefWrapper* GetInstance();
    bool Initialize();
    void Shutdown();
    bool CreateBrowser(const std::string& url);
    void LoadURL(const std::string& url);
    bool GetLatestBuffer(std::vector<uint8_t>& buffer, int& width, int& height);
    void ExecuteJavaScript(const std::string& code);

    // CefApp methods
    CefRefPtr<CefBrowserProcessHandler> GetBrowserProcessHandler() override { return this; }

    // CefBrowserProcessHandler methods
    void OnContextInitialized() override;

    // CefRenderHandler methods
    void GetViewRect(CefRefPtr<CefBrowser> browser, CefRect& rect) override;
    void OnPaint(CefRefPtr<CefBrowser> browser,
                PaintElementType type,
                const RectList& dirtyRects,
                const void* buffer,
                int width, int height) override;

private:
    CefWrapper();
    ~CefWrapper();

    static CefWrapper* instance_;
    CefRefPtr<CefBrowser> browser_;
    std::vector<uint8_t> pixel_buffer_;
    std::mutex buffer_mutex_;
    int view_width_;
    int view_height_;

    IMPLEMENT_REFCOUNTING(CefWrapper);
    DISALLOW_COPY_AND_ASSIGN(CefWrapper);
};