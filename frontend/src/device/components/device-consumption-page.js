import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import { Form, FormGroup, Label, Input, Alert } from "reactstrap";
import EnergyChart from "./EnergyChart";
import { getDeviceConsumption } from "../api/device-api";
import moment from "moment"; 

// Data de azi în format YYYY-MM-DD
const todayDateString = moment().format('YYYY-MM-DD');

const DeviceConsumptionPage = () => {
  const { deviceId } = useParams();
  const location = useLocation();
  // Preluăm deviceName trimis prin state-ul navigării
  const deviceName = location.state?.deviceName || `Device ${deviceId}`;
  
  const [consumptionData, setConsumptionData] = useState([]);
  const [selectedDate, setSelectedDate] = useState(todayDateString);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (deviceId && selectedDate) {
      fetchConsumptionData(deviceId, selectedDate);
    }
  }, [deviceId, selectedDate]);

  const fetchConsumptionData = async (id, date) => {
    setLoading(true);
    setError(null);
    try {
      // Deoarece Traefik nu expune Monitoring Service, vom avea nevoie de token de autorizare
      const data = await getDeviceConsumption(id, date); 
      setConsumptionData(data);
    } catch (err) {
      console.error(err);
      // Puteți folosi o componentă mai robustă de eroare aici
      setError(err.message || "Failed to load consumption data. Ensure Monitoring Service is running and configured correctly.");
      setConsumptionData([]); 
    } finally {
      setLoading(false);
    }
  };

  const handleDateChange = (e) => {
    setSelectedDate(e.target.value);
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">
        Energy Consumption for {deviceName}
      </h2>

      <Form inline className="mb-4 d-flex justify-content-center">
        <FormGroup>
          <Label for="dateSelect" className="me-2">Select Day:</Label>
          <Input
            type="date"
            name="date"
            id="dateSelect"
            value={selectedDate}
            onChange={handleDateChange}
            max={todayDateString} // Nu permite selectarea datelor viitoare
          />
        </FormGroup>
      </Form>

      {error && <Alert color="danger">{error}</Alert>}
      {loading && <p className="text-center">Loading consumption data...</p>}
      
      {!loading && !error && (
        <EnergyChart
          data={consumptionData}
          deviceName={deviceName}
          selectedDate={selectedDate}
        />
      )}
    </div>
  );
};

export default DeviceConsumptionPage;