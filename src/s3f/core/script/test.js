/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function paint(g) {
    var d = Math.min(200, 200);
    var ed = d / 20;
    var x = (200 - d) / 2;
    var y = (200 - d) / 2;
    // draw head (color already set to foreground)
    g.fillOval(x, y, d, d);
    g.setColor(java.awt.Color.magenta);
    g.drawOval(x, y, d, d);
    // draw eyes
    g.fillOval(x + d / 3 - (ed / 2), y + d / 3 - (ed / 2), ed, ed);
    g.fillOval(x + (2 * (d / 3)) - (ed / 2), y + d / 3 - (ed / 2), ed, ed);
    //draw mouth
    g.drawArc(x + d / 4, y + 2 * (d / 5), d / 2, d / 3, 0, -180);
    print("oi");
}
