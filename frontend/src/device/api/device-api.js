import { HOST } from "../../commons/hosts";

export const getDevices = async () => {
    const response = await fetch(`${HOST.device_service}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
    });
    
    if (!response.ok) throw new Error('Failed to fetch devices');
    const data = await response.json();  
    console.log("All devices:", data);
    return data;
};

export const getDeviceById = async (id) => {
    const response = await fetch(`${HOST.device_service}/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
    });
    if (!response.ok) throw new Error('Failed to fetch device');
    return response.json();
};

export const createDevice = async (deviceData) => {
    const response = await fetch(`${HOST.device_service}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(deviceData),
    });
    if (!response.ok) throw new Error('Failed to create device');
    return response.headers.get("Location");
};

export const updateDevice = async (id, deviceData) => {
    const response = await fetch(`${HOST.device_service}/${id}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(deviceData),
    });
    if (!response.ok) throw new Error('Failed to update device');
};

export const deleteDevice = async (id) => {
    const response = await fetch(`${HOST.device_service}/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
        },
    });
    if (!response.ok) throw new Error('Failed to delete device');
};

export const assignDevice = async (deviceId, ownerId) => {
    const response = await fetch(`${HOST.device_service}/${deviceId}/assign/${ownerId}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
        },
    });
    if (!response.ok) throw new Error('Failed to assign device');
};

export const unassignDevice = async (deviceId) => {
    const response = await fetch(`${HOST.device_service}/${deviceId}/unassign`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
        },
    });
    if (!response.ok) throw new Error('Failed to unassign device');
};


export const getMyDevices = async () => {
  const response = await fetch(`${HOST.my_device_service}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem("token")}`,
      'Content-Type': 'application/json',
    },
  });
  if (!response.ok) throw new Error('Failed to fetch devices');
  const data = await response.json();
  console.log("API response:", data);
  return data;
};


// frontend/src/device/api/device-api.js
// ...
export const getDeviceConsumption = async (deviceId, dateString) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("No auth token found. Please log in.");
    
    // URL-ul tău corect: /api/monitoring/consumption/{deviceId}?date={dateString}
    const url = `${HOST.monitoring_service}/consumption/${deviceId}?date=${dateString}`;
    
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
    });

    if (response.status === 204) {
        return []; // Returnează array gol pentru No Content
    }

    if (!response.ok) {
        // Tratează erorile 401/403/500
        const errorBody = await response.text();
        throw new Error(`Failed to fetch consumption data: ${response.status} ${response.statusText}. Details: ${errorBody}`);
    }

    // --- FIX PENTRU EROAREA 'Unexpected token <' ---
    // Citim corpul ca text pentru a preveni crash-ul la parsare.
    const responseText = await response.text();
    
    // Verifică dacă răspunsul este HTML (începe cu <)
    if (responseText.trim().startsWith('<')) {
        console.warn("Received unexpected HTML response (200 OK) instead of JSON. Assuming No Data.");
        // Returnează un array gol pentru a nu crăpa interfața.
        return [];
    }

    try {
        // Dacă nu este HTML, încearcă să-l parsezi ca JSON
        return JSON.parse(responseText);
    } catch (e) {
        console.error("Failed to parse JSON response, but it was not HTML. Body:", responseText.substring(0, 100));
        throw new Error("Failed to parse JSON response from server.");
    }
};



