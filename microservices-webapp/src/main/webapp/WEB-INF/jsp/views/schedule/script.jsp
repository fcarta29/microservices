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
            events: [{
                id: "event1",
                title: 'Server 1 - User 1',
                start: '2016-09-20T00:00:00.000Z',
                end: '2016-09-23T00:00:00.000Z',
                allDay: true,
                color: "orange",
                resources: ['server1']
            },{
                id: "event2",
                title: 'Server 2 - User 2',
                start: '2016-09-22T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "blue",
                resourceEditable: true,
                resources: ['server2']
            },{
                id: "event3",
                title: 'Server 3 - User 3',
                start: '2016-09-23T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "green",
                resources: ['server3']
            },{
                id: "event4",
                title: 'Server 4 - User 4',
                start: '2016-09-23T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "red",
                resources: ['server4']
            }],
            eventDrop: function(event, delta, revertFunc) {updateServerReservation(event);
            },
            eventResize: function(event, delta, revertFunc) {updateServerReservation(event);
            },
            resourceColumns: [{
                labelText: 'Server',
                field: 'title',
                group: true
            }],
            resources: [
                { 
                    id: 'server1',
                    title: 'Server 1',
                },{ 
                    id: 'server2',
                    title: 'Server 2'
                },{ 
                    id: 'server3',
                    title: 'Server 3'
                },{ 
                    id: 'server4',
                    title: 'Server 4'
                }
            ]
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
        getReservations();
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

    function getReservations() {        
        $.get("http://localhost:9080/api/reservations", function(reservations) {
            console.log('Getting reservations');
            $.each(reservations, function(index, reservation) {
                console.log('Reservation: ' + reservation.id);
                updateReservation(reservation);
            });
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