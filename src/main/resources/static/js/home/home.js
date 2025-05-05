	
	let calendar;
    function formatTime(date) {
        const d = new Date(date);
        const h = d.getHours().toString().padStart(2, '0');
        const m = d.getMinutes().toString().padStart(2, '0');
        return `${h}:${m}`;
    }

    function fetchSchedules() {
        if (!calendar) return;

        const schedules = [
            {
                id: '1',
                calendarId: 'dayoff',
                title: '연차',
                category: 'time',
                start: '2025-04-16',
                end: '2025-04-18',
            },
            {
                id: '2',
                calendarId: 'work',
                title: '서울 출장',
                category: 'time',
                start: '2025-04-28',
                end: '2025-04-30',
            },
            {
                id: '3',
                calendarId: 'luck',
                title: '🍀🍀🍀',
                category: 'time',
                start: '2025-04-22',
                end: '2025-04-23',
            },
            {
                id: '4',
                calendarId: 'luck',
                title: '🪷부처님 오신날🪷',
                category: 'time',
                start: '2025-05-5',
                end: '2025-05-6',
            },
            {
                id: '5',
                calendarId: 'work',
                title: '수원 출장 ✈️',
                category: 'time',
                start: '2025-05-20',
                end: '2025-05-23',
            },
			{
               id: '6',
               calendarId: 'dayoff',
               title: '연차',
               category: 'time',
               start: '2025-05-26',
               end: '2025-05-27',
           },
			{
               id: '7',
               calendarId: 'dayoff',
               title: '🌟발표일',
               category: 'time',
               start: '2025-05-7',
               end: '2025-05-7',
           },
			{
               id: '8',
               calendarId: 'luck',
               title: '자료제출',
               category: 'time',
               start: '2025-05-2',
               end: '2025-05-2',
           }
        ];

        calendar.clear();
        calendar.createEvents(schedules);
    }

    document.addEventListener('DOMContentLoaded', function () {
        calendar = new tui.Calendar('#calendar', {
            defaultView: 'month',
            template: {
                time(event) {
                    return `<span>${formatTime(event.start)}~${formatTime(event.end)} ${event.title}</span>`;
                }
            },
            calendars: [
                {
                    id: 'work',
                    name: '근무일정',
                    backgroundColor: '#fff895',
                    borderColor: '#ffc107'
                },
                {
                    id: 'luck',
                    name: 'luck',
                    backgroundColor: '#a2e662',
                    borderColor: '#22c834'
                },
                {
                    id: 'dayoff',
                    name: '휴가',
                    backgroundColor: '#ff9f89',
                    borderColor: '#ff6f61'
                },
            ]
        });

        // 버튼 이벤트
        document.getElementById('todayBtn').addEventListener('click', function () {
            calendar.today();
            fetchSchedules();
        });

        document.getElementById('nextBtn').addEventListener('click', function () {
            calendar.next();
            fetchSchedules();
        });

        document.getElementById('prevBtn').addEventListener('click', function () {
            calendar.prev();
            fetchSchedules();
        });

        fetchSchedules();
    });
	
	
	function addScheduleBtn(){
		alert("개발중입니다.");
	}
	
		document.addEventListener("DOMContentLoaded", function () {
	        const todayElement = document.getElementById("today");
	        const today = new Date();
	        
	        const yyyy = today.getFullYear();
	        const mm = String(today.getMonth() + 1).padStart(2, '0');
	        
	        todayElement.textContent = `${yyyy}년 ${mm}월`;
	    });