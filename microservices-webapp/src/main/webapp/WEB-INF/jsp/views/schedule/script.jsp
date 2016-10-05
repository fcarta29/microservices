<script src="/js/fullcalendar.min.js"></script>

<script type="text/javascript">

    $(document).ready(function() {

        $("#startDatepicker").datepicker();
        $("#endDatepicker").datepicker();
        
        $('#calendarDiv').fullCalendar({
            height: 450,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,basicWeek,basicDay'
            },            
            defaultView: 'month',
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
            var newReservation = {
                user: $('#usersList').val(),
                start: $("#startDatepicker").val(),
                end: $("#endDatepicker").val(),
                server: $('#serversList').val()
            };
            alert("TODO[fcarta] - Sending: " + JSON.stringify(newReservation));
        })

        getUsers();
        getServers();
    });
    
    function stompSubscribeHandlers() {
        stompClient.subscribe('/topic/updates', function(update){
            updateReservation(JSON.parse(update.body));
        });
    }

    function stompUnsubscribeHandlers() {
        stompClient.unsubscribe('/topic/updates');
    }

    function getUsers() {
        $.get("http://localhost:9080/api/users", function(users) {
            console.log('Getting users');
            $.each(users, function(index, user) {
                console.log('User: ' + user.name);
                updateUser(user);
            });
        });
    }

    function getServers() {
        $.get("http://localhost:9080/api/servers", function(servers) {
            console.log('Getting servers');
            $.each(servers, function(index, server) {
                console.log('Server: ' + server.name);
                updateServer(server);
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
               // $('#calendarDiv').fullCalendar('addEvent', event);
            });
            
            callback(schedule.events);
        });
    }

    function updateServerReservation(event) {
        confirm("Send Server Reservation update? " + event.title + " " + event.start.format() + " " + event.end.format());
    }    

    function updateUser(user) {
        $('#usersList').append($("<option></option>")
                .attr("value",user.name)
                .text(user.name));
    }

    function updateServer(server) {
        $('#serversList').append($("<option></option>")
                .attr("value",server.name)
                .text(server.name));
    }
    
    function updateReservation(reservation) {
        //var response = $('#response');
        //var p = document.createElement('p');
        //p.style.wordWrap = 'break-word';
        //p.appendChild(document.createTextNode(reservation.id 
        //        + ", " + reservation.name
        //        + ", " + reservation.server_name
        //        + ", " + reservation.start_date
        //        + ", " + reservation.end_date
        //        + ", " + reservation.approved));
        //response.append(p);
    }
</script>