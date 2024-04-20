package electionledger.commonGUI;

import electionledger.node.RemoteInterface;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.BorderLayout;
import java.rmi.RemoteException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class pieChart extends JPanel {

    private PieDataset pieDataset;                  //dataset 
    private JFreeChart pieChart;                    //componente base
    private ChartPanel chartPanel;                  //componente do grafico
    private CopyOnWriteArrayList<String> candidates;//Array list concorrente de candidatos
    private RemoteInterface remote;                 //Objeto remoto

    public pieChart(RemoteInterface remote) throws RemoteException {
        this.remote = remote;
        candidates = remote.getCandidates();
        initComponents();
        showPieChart();
    }

    /**
     * Exibe o gráfico na interface gráfica.
     *
     * @throws RemoteException Lança exceção em caso de erro no objeto remoto
     */
    public void showPieChart() throws RemoteException {
        pieDataset = createDataset();
        pieChart = createChart(pieDataset);
        chartPanel = new ChartPanel(pieChart);
        removeAll();
        add(chartPanel, BorderLayout.CENTER);
        chartPanel.validate();
    }

    /**
     * Cria o conjunto de dados para o gráfico de pizza.
     *
     * @return Conjunto de dados para o gráfico
     * @throws RemoteException Lança exceção em caso de erro no objeto remoto
     */
    private PieDataset createDataset() throws RemoteException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (String candidate : candidates) {
            // Adiciona os votos do candidato ao conjunto de dados
            dataset.setValue(candidate, remote.getResults(candidate, remote.masterPrivateKey()));
            System.out.println("Votos" + remote.getResults(candidate, remote.masterPrivateKey()));
        }
        return dataset;
    }

    /**
     * Cria o gráfico com base no conjunto de dados fornecido.
     *
     * @param dataset Conjunto de dados para o gráfico
     * @return Gráfico configurado
     */
    private JFreeChart createChart(PieDataset dataset) {
        // Cria um gráfico de pizza 3D com o título "Estatísticas das eleições"
        return ChartFactory.createPieChart3D("Estatísticas das eleições", dataset, true, true, false);
    }

    /**
     * Inicializa os componentes da interface gráfica.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel jLabel1 = new JLabel("Estatísticas");
        pieChartPanel = new JPanel();

        add(jLabel1, BorderLayout.NORTH);
        add(pieChartPanel, BorderLayout.CENTER);
    }

    private JPanel pieChartPanel;
}
