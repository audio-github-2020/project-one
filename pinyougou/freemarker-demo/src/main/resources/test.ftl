<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker入门小DEMO</title>
    <#--
     #assign:定义一个简单的值类型
            ：可以定义一个对象[JSON]


     -->
    <#assign linkman="Link"/>
    <#assign userinfo={"mobile":"131660333","address":"MH"}/>
</head>
<body>
<pre>
    <#--我只是一个注释，我不会有任何输出  -->
    ${username},你好。${now}


<div>
    <h4>#assign</h4>
    ${linkman}!
    Phone number:${userinfo.mobile} <br/>
    Address:${userinfo.address}


</div>


    </pre>
</body>
</html>
