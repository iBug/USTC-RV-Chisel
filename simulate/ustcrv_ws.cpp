#include <unordered_set>
#include <mutex>
#include <sstream>
#include <thread>
#include <inttypes.h>
#include <verilated.h>
#include <verilated_vcd_c.h>
#include "crow_all.h"
#include "VMainWithClock.h"

int main(int argc, char **argv) {
    crow::SimpleApp app;
    std::unordered_set<crow::websocket::connection *> users;
    std::mutex mtx;
    VMainWithClock *top = new VMainWithClock;
    int64_t main_time = 0;
    std::thread sim([&]() {
        while (!Verilated::gotFinish()) {
            
            ++main_time;
            if (main_time % 10 == 3) {
                top->clock = 0;
            } else if (main_time % 10 == 8) {
                top->clock = 1;
            }
            top->eval();
            if (main_time % 100000 == 0) {
                std::ostringstream ss;
                ss << "{\"time\":" << main_time << ",\"led\":" << int(top->io_LED)
                   << ",\"numA\":" << top->MainWithClock__DOT__Main__DOT__SegmentDisplay_io_numA
                   << "}";
                std::lock_guard<std::mutex> _(mtx);
                for (auto u : users) {
                    u->send_text(ss.str());
                }
            }

        }
    });
    CROW_ROUTE(app, "/ws")
        .websocket()
        .onopen([&](crow::websocket::connection &conn){
            std::lock_guard<std::mutex> _(mtx);
            users.insert(&conn);
        }).onclose([&](crow::websocket::connection &conn, const std::string &reason) {
            std::lock_guard<std::mutex> _(mtx);
            users.erase(&conn);
        }).onmessage([&](crow::websocket::connection &, const std::string &data, bool is_binary){
            std::lock_guard<std::mutex> _(mtx);
            auto req = crow::json::load(data);
            if (!req) {
                return;
            }
            top->reset = req["reset"].i();
            top->io_SW = req["sw"].i();
        });
    CROW_ROUTE(app, "/")
    ([]{
        return R"A(
<html>
    <head>
        <meta charset="utf-8">
        <title>Simulator</title>
    </head>
    <body>
        <table>
            <tr>
                <th>LED0</th>
                <th>LED1</th>
                <th>LED2</th>
                <th>LED3</th>
                <th>LED4</th>
                <th>LED5</th>
                <th>LED6</th>
                <th>LED7</th>
                <th>LED8</th>
                <th>LED9</th>
                <th>LED10</th>
                <th>LED11</th>
                <th>LED12</th>
                <th>LED13</th>
                <th>LED14</th>
                <th>LED15</th>
            </tr>
            <tr>
                <td id="led0"></td>
                <td id="led1"></td>
                <td id="led2"></td>
                <td id="led3"></td>
                <td id="led4"></td>
                <td id="led5"></td>
                <td id="led6"></td>
                <td id="led7"></td>
                <td id="led8"></td>
                <td id="led9"></td>
                <td id="led10"></td>
                <td id="led11"></td>
                <td id="led12"></td>
                <td id="led13"></td>
                <td id="led14"></td>
                <td id="led15"></td>
            </tr>
        </table>
        <table>
            <tr><th scope="col">time</th><td id="time"></td></tr>
            <tr><th scope="col">numA</th><td id="numA"></td></tr>
        </table>
        <div><label>RESET</label><input type="checkbox" id="reset"></div>
        <table>
            <tr>
                <th>SW0</th>
                <th>SW1</th>
                <th>SW2</th>
                <th>SW3</th>
                <th>SW4</th>
                <th>SW5</th>
                <th>SW6</th>
                <th>SW7</th>
                <th>SW8</th>
                <th>SW9</th>
                <th>SW10</th>
                <th>SW11</th>
                <th>SW12</th>
                <th>SW13</th>
                <th>SW14</th>
                <th>SW15</th>
            </tr>
            <tr>
                <td><input type="checkbox" id="sw0"></td>
                <td><input type="checkbox" id="sw1"></td>
                <td><input type="checkbox" id="sw2"></td>
                <td><input type="checkbox" id="sw3"></td>
                <td><input type="checkbox" id="sw4"></td>
                <td><input type="checkbox" id="sw5"></td>
                <td><input type="checkbox" id="sw6"></td>
                <td><input type="checkbox" id="sw7"></td>
                <td><input type="checkbox" id="sw8"></td>
                <td><input type="checkbox" id="sw9"></td>
                <td><input type="checkbox" id="sw10"></td>
                <td><input type="checkbox" id="sw11"></td>
                <td><input type="checkbox" id="sw12"></td>
                <td><input type="checkbox" id="sw13"></td>
                <td><input type="checkbox" id="sw14"></td>
                <td><input type="checkbox" id="sw15"></td>
            </tr>
        </table>
        <button type="button" id="set">set</button>
        <script type="text/javascript">
            var ws = new WebSocket('ws://127.0.0.1:8080/ws');
            ws.onmessage = function(event) {
                var data = JSON.parse(event.data);
                document.getElementById('numA').innerHTML = data['numA'];
                document.getElementById('time').innerHTML = data['time'];
                for (var i = 0; i < 16; ++i) {
                    var led_id = 'led' + i;
                    var e = document.getElementById(led_id);
                    var on = data['led'] & (1 << i);
                    e.innerHTML = on ? 'ON' : 'OFF';
                    e.style.backgroundColor = on ? 'green' : 'transparent';
                }
            };
            document.getElementById('set').onclick = function() {
                var sw = 0;
                for (var i = 0; i < 16; ++i) {
                    var sw_id = 'sw' + i;
                    var e = document.getElementById(sw_id);
                    sw += (e.checked ? 1 : 0) << i;
                }
                var data = {
                    "reset" : document.getElementById('reset').checked ? 1 : 0,
                    "sw" : sw
                };
                ws.send(JSON.stringify(data));
                return false;
            };
        </script>
    </body>
</html>
        )A";
    });
    int port = 8080;
    if (argc == 2) {
        port = atoi(argv[1]);
    }
    app.port(port).run();
    return 0;
}