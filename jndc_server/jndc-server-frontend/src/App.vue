<template>
    <div id="app">
        <transition>
            <router-view></router-view>
        </transition>
    </div>
</template>

<script>

    export default {
        name: 'App',
        computed: {
            key() {
                return this.$route.path
            }
        }
    }
</script>

<style>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        color: #2c3e50;
        margin: 0px;
    }

    body {
        margin: 0;
    }
</style>
