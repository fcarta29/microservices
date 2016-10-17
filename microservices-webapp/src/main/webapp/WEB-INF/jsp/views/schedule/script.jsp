<script src="/js/fullcalendar.min.js"></script>

<script type="text/javascript">

    $(document).ready(function() {

        $("#startDatepicker").datepicker({
            dateFormat: "yy-mm-dd",
            changeMonth: true,
            changeYear: true,
            autoSize: true
        });
        $("#endDatepicker").datepicker({
            dateFormat: "yy-mm-dd",
            changeMonth: true,
            changeYear: true,
            autoSize: true
        });

        $('#calendarDiv').fullCalendar({
            height: 450,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,basicWeek,basicDay'
            },
            defaultView: 'basicWeek',
            views: {
                agendaWeek: {
                    groupByResource: true
                },
                basicWeek: {
                    groupByResource: true
                }
            },
            editable: true,
            events: function(start, end, timezone, callback) {
            	getSchedule(start,end,callback);
            },
            eventDrop: function(event, delta, revertFunc) {
            	updateServerReservation(event);
            },
            eventResize: function(event, delta, revertFunc) {
            	updateServerReservation(event);
            },
            resourceColumns: [{
                labelText: 'Server',
                field: 'title',
                group: true
            }],
            resources: []
        });

        $("#submitReservationBtn").click(function() {
            var startDate = new Date($("#startDatepicker").val());
            var endDate = new Date($("#endDatepicker").val());
            endDate.setUTCDate(endDate.getUTCDate() + 1);

        	var reservation = {
        		name: $('#title').val(),
       		    owner_name: $('#usersList').val(),
       		    server_name: $('#serversList').val(),
       		    start_date: startDate.toISOString(),
                end_date: endDate.toISOString()
            };

            $.ajax({
                url: "http://localhost:9080/api/reservations",
                type: "POST",
                dataType: "json",
                data: JSON.stringify(reservation),
                contentType: "application/json",
                success: function(data,status) {
                    var event = {
                    	id: data.id,
                    	title: "UNAPPROVED-" + data.name + "\n(" + data.server_name + " - " + data.owner_name + ")",
                    	start: data.start_date,
                    	end: data.end_date,
                    	color: "lightgray",
                   	    allDay: true
                    }
                    addEvent(event);
                }
            });
        })

        getUsers();
        getServers();
    });

    function stompSubscribeHandlers() {
        stompClient.subscribe('/topic/events/add', function(event){
        	updateEvent(JSON.parse(event.body));
        });
        stompClient.subscribe('/topic/events/remove', function(event){
            removeEvent(JSON.parse(event.body));
        });
    }

    function stompUnsubscribeHandlers() {
        stompClient.unsubscribe('/topic/events/add');
        stompClient.unsubscribe('/topic/events/remove');
    }

    function getUsers() {
        $.get("http://localhost:9080/api/users", function(users) {
            console.log('Getting users');
            $.each(users, function(index, user) {
                console.log('User: ' + user.name);
                addUser(user);
            });
        });
    }

    function getServers() {
        $.get("http://localhost:9080/api/servers", function(servers) {
            console.log('Getting servers');
            servers.sort(function(a, b) {
                tA = a.name.toUpperCase();
                tB = b.name.toUpperCase();
                return (tA < tB) ? -1 : (tA > tB) ? 1 : 0;
            });
            $.each(servers, function(index, server) {
                console.log('Server: ' + server.name);
                addServer(server);
            });
        });
    }

    function getSchedule(start,end,callback) {
    	var queryString = "";
    	if (typeof start !== 'undefined') {
    		queryString += "?start="+start;
    		if (typeof end !== 'undefined') {
    			queryString += "&end="+end;
    		}
    	}
        $.get("http://localhost:9080/api/schedule" + queryString, function(schedule) {
            console.log('Getting schedule');

            $.each(schedule.events, function(index, event) {
                console.log('Event: ' + event.id);
            });

            callback(schedule.events);
        });
    }

    function updateServerReservation(event) {
        confirm("NOT Implemented! - Send Server Reservation update? " + event.title + " " + event.start.format() + " " + event.end.format());
    }

    function addUser(user) {
        $('#usersList').append($("<option></option>")
                .attr("value",user.name)
                .text(user.name));
    }

    function addServer(server) {
        $('#serversList').append($("<option></option>")
                .attr("value",server.name)
                .text(server.name));
    }

    function addEvent(event) {
        $('#calendarDiv').fullCalendar('renderEvent', event);
    }

    function removeEvent(event) {
        $('#calendarDiv').fullCalendar('removeEvents', event.id);
    }

    function updateEvent(event) {
        removeEvent(event);
        addEvent(event);
    }


</script>
