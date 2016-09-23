<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed"
                data-toggle="collapse" data-target="#navbar" aria-expanded="false"
                aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span> <span
                    class="icon-bar"></span> <span class="icon-bar"></span> <span
                    class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">Microservices - Demo</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
        ${model.menu.menuItem}
        <c:out value="${menu.menuItem}" />
        <c:out value="${model.menu.menuItem}" />
            <ul class="nav navbar-nav">
                <li class="${menu.menuItem == 'home' ? 'active' : ''}"><a href="/schedule">Schedule</a></li>
                <li class="${menu.menuItem == 'about' ? 'active' : ''}"><a href="/about">About</a></li>

            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</nav>