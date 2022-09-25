import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.button
import react.useState

// 自定义组件
external interface LinkProps : Props {
    var href: String
    var c: Int
    var onAdd:(Int)->Unit
}

val Link = FC<LinkProps> { props ->
    val (count, setCount) = useState(0)
    var c by useState(props.c)
    a {
        href = props.href
        +"Click me $count times $c"
    }
    button {
        onClick = {
            setCount(count + 1)
            c++
            props.onAdd(c)
        }
        +"Click me"
    }
}
