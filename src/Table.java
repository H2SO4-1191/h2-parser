import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Table extends JScrollPane {
    JTable table;
    public Table(DefaultTableModel model) {
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setBackground(Main.tableBackground);
        table.setForeground(Main.zoeYellow);
        table.getTableHeader().setBackground(Main.textAreaBackground);
        table.getTableHeader().setForeground(Main.mioMagenta);
        table.setFont(Main.customFont);
        table.getTableHeader().setFont(Main.customFont);
        table.setDefaultRenderer(Object.class, table.getDefaultRenderer(Object.class));
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setHorizontalAlignment(SwingConstants.CENTER);
        setViewportView(table);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getViewport().setBackground(Main.tableBackground);
        setBackground(Main.textAreaBackground);
        JScrollBar[] bars = { getVerticalScrollBar(), getHorizontalScrollBar() } ;
        for(JScrollBar bar: bars) {
            UIManager.put("ScrollBar.thumb", new ColorUIResource(Main.zoeYellow));
            bar.setBackground(Main.textAreaBackground);
            bar.setUI(new BasicScrollBarUI());
            bar.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors(){
                    thumbColor = Main.zoeYellow;
                }
            });
        }
    }
}
