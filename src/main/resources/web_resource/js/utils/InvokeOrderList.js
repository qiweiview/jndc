class InvokeOrderList {
    invokeList = []
    point = 0


    register(x) {
        this.invokeList.push(x)

    }

    hasNextElement() {
        return this.point < this.invokeList.length - 1
    }

    nextElement() {
        let s = this.invokeList[point++]
        return s
    }

    run() {
        return arun(this.invokeList)
    }


}


async function arun(invokeList) {
    console.log(invokeList.length)
    const routes = []
    for (let i=0; i < invokeList.length; i++) {
        let z = invokeList[i];
        let r = await z.invoke()
        routes.push(r)
    }
    return routes
}

