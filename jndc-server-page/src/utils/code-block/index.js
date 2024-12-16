import "./index.scss";

import "highlight.js/styles/base16/material-palenight.css";
import "highlight.js/styles/base16/silk-light.css";

import hljs from "highlight.js/lib/core";
import javascript from "highlight.js/lib/languages/javascript";
import vbscript from "highlight.js/lib/languages/vbscript";
import python from "highlight.js/lib/languages/python";
import matlab from "highlight.js/lib/languages/matlab";
import csharp from "highlight.js/lib/languages/csharp";
import shell from "highlight.js/lib/languages/shell";
import vhdl from "highlight.js/lib/languages/vhdl";
import java from "highlight.js/lib/languages/java";
import css from "highlight.js/lib/languages/css";
import xml from "highlight.js/lib/languages/xml";
import sql from "highlight.js/lib/languages/sql";
import cpp from "highlight.js/lib/languages/cpp";
import c from "highlight.js/lib/languages/c";
import bash from "highlight.js/lib/languages/bash";
import go from "highlight.js/lib/languages/go";
import kotlin from "highlight.js/lib/languages/kotlin";
import scss from "highlight.js/lib/languages/scss";
import typescript from "highlight.js/lib/languages/typescript";
import php from "highlight.js/lib/languages/php";
import ruby from "highlight.js/lib/languages/ruby";
import yaml from "highlight.js/lib/languages/yaml";
import dockerfile from "highlight.js/lib/languages/dockerfile";
import json from "highlight.js/lib/languages/json";
import ClipboardJS from "clipboard";
import { message as toast } from "@/utils/message";

hljs.registerLanguage("javascript", javascript);
hljs.registerLanguage("vbscript", vbscript);
hljs.registerLanguage("python", python);
hljs.registerLanguage("matlab", matlab);
hljs.registerLanguage("csharp", csharp);
hljs.registerLanguage("shell", shell);
hljs.registerLanguage("vhdl", vhdl);
hljs.registerLanguage("java", java);
hljs.registerLanguage("html", xml);
hljs.registerLanguage("xml", xml);
hljs.registerLanguage("css", css);
hljs.registerLanguage("sql", sql);
hljs.registerLanguage("cpp", cpp);
hljs.registerLanguage("c", c);
hljs.registerLanguage("bash", bash);
hljs.registerLanguage("go", go);
hljs.registerLanguage("kotlin", kotlin);
hljs.registerLanguage("scss", scss);
hljs.registerLanguage("typescript", typescript);
hljs.registerLanguage("php", php);
hljs.registerLanguage("ruby", ruby);
hljs.registerLanguage("yaml", yaml);
hljs.registerLanguage("dockerfile", dockerfile);
hljs.registerLanguage("json", json);

hljs.configure({ ignoreUnescapedHTML: true });

/**
 * 高亮代码块
 * @param {Element} element 包含 pre code 代码块的元素
 */
function highlightCode(element) {
  const codeEls = element.querySelectorAll("pre code");
  codeEls.forEach(el => {
    hljs.highlightElement(el);
  });
}

/**
 * 给代码块添加行号
 * @param {Element} element 包含 pre code 代码块的元素
 */
function buildLineNumber(element) {
  let codes = element.querySelectorAll("pre code");
  if (!codes.length) {
    return false;
  }

  codes.forEach(code => {
    if (!code.classList.contains("hljsln")) {
      code.classList.add("hljsln");
      code.innerHTML = addLineNumbersFor(code.innerHTML);
      var lineNumbers = code.querySelectorAll("span[data-num]");
      var lastNum = lineNumbers[lineNumbers.length];
      if (lastNum && !lastNum.innerHTML) {
        lastNum.remove();
      }
    }
  });
}

function addLineNumbersFor(html) {
  var text = html.replace(/<span[^>]*>|<\/span>/g, "");
  if (/\r|\n$/.test(text)) {
    html += '<span class="ln-eof"></span>';
  }
  var num = 1;
  html = html.replace(/\r\n|\r|\n/g, function (a) {
    num++;
    return a + '<span class="ln-num" data-num="' + num + '"></span>';
  });
  html = '<span class="ln-num" data-num="1"></span>' + html;
  html = '<span class="ln-bg"></span>' + html;
  return html;
}
/**
 * 给代码块添加复制按钮
 * @param {Element} element 包含 pre code 代码块的元素
 */
function buildCopyButton(element) {
  let pres = element.querySelectorAll("pre");
  if (!pres.length) return;

  pres.forEach(function (pre) {
    var code = pre.querySelector("code").textContent;
    const match = pre
      .querySelector("code")
      .classList[0].match(/language-(\w+)/);
    const language = match ? match[1] : "未找到语言";
    console.log(language);
    // 创建按钮
    var btn = document.createElement("span");

    btn.className = "copy";
    btn.setAttribute("data-clipboard-text", code);
    // 添加SVG图标
    var svgIcon = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svgIcon.setAttribute("xmlns", "http://www.w3.org/2000/svg"); // 添加xmlns属性
    svgIcon.setAttribute("width", "1em"); // 添加width属性
    // 其他SVG属性
    svgIcon.setAttribute("class", "copy-icon"); // 添加class属性
    svgIcon.setAttribute("height", "24"); // 添加height属性
    svgIcon.setAttribute("viewBox", "0 0 24 24"); // 添加viewBox属性
    svgIcon.setAttribute("fill", "none"); // 添加其他属性
    svgIcon.innerHTML = `
<path fill="currentColor" d="M9 18q-.825 0-1.413-.588T7 16V4q0-.825.588-1.413T9 2h9q.825 0 1.413.588T20 4v12q0 .825-.588 1.413T18 18H9Zm-4 4q-.825 0-1.413-.588T3 20V6h2v14h11v2H5Z"/>
    `;

    btn.appendChild(svgIcon);
    pre.insertBefore(btn, pre.firstChild);

    var clipboard = new ClipboardJS(btn);
    clipboard.on("success", function () {
      toast("复制成功", { type: "success" });
    });
    clipboard.on("error", function () {
      toast("复制失败", { type: "warning" });
    });
  });
}

/**
 * 创建代码块
 * @param {string} selector 包含 pre code 的元素选择器
 */
function buildCodeBlock(selector) {
  let elements = document.querySelectorAll(selector);
  for (let element of elements) {
    highlightCode(element);
    buildLineNumber(element);
    buildCopyButton(element);
  }
}

export default buildCodeBlock;
