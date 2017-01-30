<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="chrome=1">
    <title>IK.AM by @making</title>

    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" href="css/pygment_trac.css">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
</head>
<body>
<div class="wrapper">
    <header>
        <h1>IK.AM</h1>
        <p><a href="https://twitter.com/making">@making</a>'s page<br>
            Keep on Hacking ☕️ 🍺</p>
        <p>
            <a href="http://www.amazon.co.jp/exec/obidos/ASIN/4777519694/ikam-22/ref=nosim/" name="amazletlink"
               target="_blank"><img src="https://images-fe.ssl-images-amazon.com/images/I/51MBlgJ0pTL._SL160_.jpg"
                                    alt="はじめてのSpring Boot―スプリング・フレームワークで簡単Javaアプリ開発 (I・O BOOKS)" style="border: none;"/></a>
            <a href="http://www.amazon.co.jp/exec/obidos/ASIN/4798142476/ikam-22/ref=nosim/" name="amazletlink"
               target="_blank"><img src="https://images-fe.ssl-images-amazon.com/images/I/6160VVxyCcL._SL160_.jpg"
                                    alt="Spring徹底入門 Spring FrameworkによるJavaアプリケーション開発" style="border: none;"/></a>
            <a href="http://www.amazon.co.jp/exec/obidos/ASIN/4774183164/ikam-22/ref=nosim/" name="amazletlink"
               target="_blank"><img src="https://images-fe.ssl-images-amazon.com/images/I/51RVOHYy%2BXL._SL160_.jpg"
                                    alt="パーフェクト Java EE" style="border: none;"/></a>
        </p>
    </header>
    <section>
        <h2>Blog</h2>
        <ul>
        <#list entries as entry>
            <li>${entry.updatedAt} <a href="https://blog.ik.am/entries/${entry.entryId}">${entry.title}</a></li>
        </#list>
        </ul>
    </section>
    <section>
        <h2>Github</h2>
        <ul>
        <#list events as event>
            <li>${event.createdAt} [${event.type}] <a href="https://github.com/${event.repo}">${event.repo}</a>
                <#if event.message??><br>📝 <a
                        href="${event.url}">${event.message}</a><#elseif event.url??>
                    <br><a　href="${event.url}">🔎</a></#if>
            </li>
        </#list>
        </ul>
    </section>
    <footer>
        <p>
            <small>IK.AM &mdash; &copy; 2013-2017 Toshiaki Maki</small>
        </p>
    </footer>
</div>
</body>
</html>