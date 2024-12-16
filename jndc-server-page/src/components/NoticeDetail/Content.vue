<script setup lang="ts">
import { nextTick, watch } from "vue";
import buildCodeBlock from "@/utils/code-block/index";

const props = defineProps({
  content: {
    type: String
  },
  contentHeight: {
    type: String,
    default: "67vh"
  }
});
watch(
  () => props.content,
  () => {
    nextTick(() => {
      buildCodeBlock(".notice-content");
    });
  }
);
</script>
<template>
  <div
    :style="{ height: props.contentHeight }"
    class="notice-content"
    v-html="content"
  />
</template>

<style lang="scss">
.notice-content {
  overflow-y: auto;
  max-height: 76vh;
  p,
  span,
  ul,
  li {
    letter-spacing: 2px;
  }

  h1 code,
  h2 code,
  h3 code,
  h4 code,
  h5 code,
  h6 code,
  p > code,
  li > code,
  table code {
    line-height: 1.2;
    font-family: consolas !important;
    vertical-align: middle;
    margin: 0 3px;
    background-color: #f9f2f4 !important;
    font-size: 13px !important;
    padding: 0.2em 0.3em !important;

    border: 1px solid #f9f2f4 !important;
  }

  /* 代码样式 */
  pre {
    white-space: pre;
    position: relative;
    border-radius: 3px;
    font-size: 14px;
    margin: 10px 0;
    padding: 0;
    code {
      border: none;
      border-radius: 7px;
      font-family: Consolas, Monaco, "Andale Mono", "Ubuntu Mono", monospace !important;
      line-height: 22.5px;
    }
  }

  kbd {
    background-color: #f7f7f7;
    color: #222325;
    border-radius: 0.25rem;
    border: 1px solid #cbcccd;
    box-shadow: 0 2px 0 1px #cbcccd;
    cursor: default;
    font-family: Arial, sans-serif;
    font-size: 0.75em;
    line-height: 1;
    min-width: 0.75rem;
    padding: 2px 5px;
    position: relative;
    top: -1px;

    &:hover {
      box-shadow: 0 1px 0 0.5px #cbcccd;
      top: 1px;
    }
  }

  a {
    text-decoration: none;
    transition: all 0.3s ease;
    position: relative;

    &::after {
      content: "";
      display: block;
      width: 0;
      height: 1px;
      position: absolute;
      left: 0;
      bottom: -2px;
      transition: all 0.3s ease-in-out;
    }

    &:hover::after {
      width: 100%;
    }
  }

  blockquote {
    background-color: #ecf5ff;
    border-left: 8px solid var(--el-color-primary);
    display: block;
    font-size: 100%;
    line-height: 1.5;
    margin: 10px 0;
    padding: 10px;
    margin-right: 10px;
  }
}
</style>
