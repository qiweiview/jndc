package jndc_server.web_support.http_module;

public class ServerRuntimeConfig {
    public static boolean DEBUG_MODEL=false;//only be changed on runtime,init value must be false

    public static String ROUTE_NOT_FOUND_CONTENT="<!doctype html><html><head><meta charset=\"UTF-8\"><title>404 Not Found</title><style>body{margin:0;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",sans-serif;background:#f7f8fa;color:#1f2937;display:flex;align-items:center;justify-content:center;min-height:100vh}.card{padding:40px 48px;background:#fff;border:1px solid #e5e7eb;border-radius:16px;box-shadow:0 10px 30px rgba(15,23,42,.08);text-align:center}h1{margin:0 0 12px;font-size:40px}p{margin:0;font-size:16px;color:#6b7280}</style></head><body><div class=\"card\"><h1>404 Not Found</h1><p>The requested route is not configured on this server.</p></div></body></html>";


}
