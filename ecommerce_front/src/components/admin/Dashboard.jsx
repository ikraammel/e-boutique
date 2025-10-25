import React, { useState, useEffect } from 'react';
import { LineChart, Line, PieChart, Pie, BarChart, Bar,Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Clock, DollarSign, ShoppingCart, Package, Users, TrendingUp, TrendingDown } from 'lucide-react';
import { dashboardService } from '../Api';
import './Dashboard.css';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

const StatCard = ({ title, value, trend, trendValue }) => (
  <div className="stat-card">
    <h3>{title}</h3>
    <p>{value}</p>
    {trend && <div className={`trend ${trend}`}>{trend === 'up' ? <TrendingUp /> : <TrendingDown />}{trendValue}%</div>}
  </div>
);

const Dashboard = () => {
  const [stats, setStats] = useState({});
  const [salesData, setSalesData] = useState([]);
  const [categorySales, setCategorySales] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDays, setSelectedDays] = useState(7);

  useEffect(() => { loadDashboardData(); }, [selectedDays]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [statsRes, salesRes, productsRes, ordersRes, categoryRes] = await Promise.all([
        dashboardService.getStats(),
        dashboardService.getSalesChart(selectedDays),
        dashboardService.getTopProducts(5),
        dashboardService.getRecentOrders(5),
        dashboardService.getCategorySales()
      ]);

      console.log('Stats:', statsRes.data);
    console.log('Sales data:', salesRes.data);
    console.log('Top products:', productsRes.data);
    console.log('Recent orders:', ordersRes.data);
    console.log('Category sales:', categoryRes.data);

      setStats(statsRes.data);
      setSalesData(salesRes.data);
      setTopProducts(productsRes.data);
      setRecentOrders(ordersRes.data);
      setCategorySales(categoryRes.data);
    } catch(err) {
      console.error(err);
    } finally { setLoading(false); }
  };

  const formatCurrency = (value) => new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value || 0);
  const formatDate = (date) => new Date(date).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
  const getStatusLabel = (status) => {
    const labels = { PENDING:'En attente', DELIVERED:'Livrée', SHIPPED:'Expédiée', CANCELLED:'Annulée', PROCESSING:'En cours'};
    return labels[status] || status;
  }

  if (loading) return <div style={{textAlign:'center', marginTop:'50px'}}>Chargement...</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Dashboard Admin</h1>
        <p>Vue d'ensemble de votre e-commerce</p>
      </div>

      <div className="stats-grid">
        <StatCard title="Revenus Total" value={formatCurrency(stats.totalRevenue)} trend={stats.revenueChange>0?'up':stats.revenueChange<0?'down':null} trendValue={stats.revenueChange?.toFixed(1)} />
        <StatCard title="Commandes" value={stats.totalOrders} />
        <StatCard title="Produits" value={stats.totalProducts} />
        <StatCard title="Clients" value={stats.totalCustomers} />
      </div>

      {stats.pendingOrders > 0 && <div className="alert"><Clock /> {stats.pendingOrders} commande(s) en attente</div>}

      <div className="charts-grid">
        <div className="chart-card">
          <h3>Évolution des ventes</h3>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={salesData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" tickFormatter={formatDate} />
              <YAxis />
              <Tooltip formatter={formatCurrency} labelFormatter={formatDate} />
              <Line type="monotone" dataKey="total" stroke="#3b82f6" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
          <h3>Ventes par catégorie</h3>
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={categorySales} dataKey="arg2" cx="50%" cy="50%" outerRadius={100} label={({ arg0, percent }) => `${arg0} (${(percent*100).toFixed(0)}%)`}>
                {categorySales.map((entry,index)=><Cell key={index} fill={COLORS[index%COLORS.length]} />)}
              </Pie>
              <Tooltip formatter={formatCurrency} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
          <h3>Top 5 produits (Revenus)</h3>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={topProducts}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="productName" />
              <YAxis />
              <Tooltip formatter={formatCurrency} />
              <Bar dataKey="revenue" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </div>

      </div>

      <div className="charts-grid">
        <div className="top-products">
          <h3>Top 5 Produits</h3>
          {topProducts.map((p,i)=>(
            <div key={i} className="top-product-item">
              <div>
                <div className="top-product-rank">{i+1}</div>
                <span>{p.productName} ({p.totalSold} vendus)</span>
              </div>
              <div>{formatCurrency(p.revenue)}</div>
            </div>
          ))}
        </div>

        <div className="recent-orders">
          <h3>Commandes récentes</h3>
          <table>
            <thead>
              <tr><th>Client</th><th>Montant</th><th>Statut</th></tr>
            </thead>
            <tbody>
              {recentOrders.map(o=>(
                <tr key={o.id}>
                  <td>{o.arg1} <br/> <small>{o.arg2}</small></td>
                  <td>{formatCurrency(o.total)}</td>
                  <td><span className={`status ${o.arg4}`}>{getStatusLabel(o.arg4)}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
