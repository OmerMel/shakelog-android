
# ShakeLog - Web Dashboard

**ShakeLog Web Dashboard** is the administrative frontend for the ShakeLog bug reporting ecosystem. Built with **React** and **Vite**, it provides a responsive interface for development teams to monitor, filter, and manage bug reports submitted from mobile devices.

## Key Features

-   **Secure Access:** Project-based login system using Project ID and Access Codes.
    
-   **Smart Filtering:** Filter reports by Status (Open, In Progress, Resolved) and search by content/ID.
    
-   **Detailed Inspection:** Expandable rows showing screenshots, device metadata, and user descriptions.
    
-   **Log Management:** Generate and download structured `.txt` logs directly from the browser, including deep links to external Logcat files (Firestore).
    
-   **Interactive UI:** Update report statuses directly from the grid with optimistic UI updates.
    
-   **Theme Support:** Fully responsive design with built-in Dark/Light mode.
    

## Technology Stack

-   **Framework:** React 18
    
-   **Build Tool:** Vite
    
-   **Styling:** Tailwind CSS
    
-   **Icons:** Google Material Symbols
    
-   **State Management:** React Context API + Hooks
    

## Getting Started

Follow these instructions to set up the dashboard locally.

### Prerequisites

-   Node.js 18+
    
-   npm or yarn
    

### Installation

1.  **Clone the repository:**
    
    ```
    git clone [https://github.com/OmerMel/shakelog-dashboard.git](https://github.com/OmerMel/shakelog-dashboard.git)
    cd shakelog-dashboard
    
    ```
    
2.  **Install dependencies:**
    
    ```
    npm install
    
    ```
    
3.  **Run the development server:**
    
    ```
    npm run dev
    
    ```
    
4.  **Open in Browser:**
    
    Navigate to `http://localhost:5173` to view the dashboard.
    

## Code Structure & Components

The project is structured to separate view logic, state management, and API services. Below is an overview of the key components in the codebase:

### 1. Components (`src/components`)

-   **`AuthPage.jsx`**: Handles the entry point of the application. Manages both the **Login** form (for existing projects) and the **Create Project** form. It interacts with the `AuthContext` to store session data.
    
-   **`Header.jsx`**: The persistent top navigation bar. It displays the current project name, the API Key (with copy functionality), the Theme Toggle button, and the Logout action.
    
-   **`StatsOverview.jsx`**: Computes and displays the top-level metrics (e.g., Number of Open Bugs, Resolved Today). It uses `useMemo` to efficiently recalculate stats when the reports array changes.
    
-   **`ReportTable.jsx`**: The core component of the dashboard.
    
    -   **Filtering Logic:** Implements client-side filtering based on search terms and status selection.
        
    -   **Row Expansion:** Handles the "Accordion" logic to show/hide the detailed view (screenshot + metadata).
        
    -   **Log Generation:** Contains the logic to generate `.txt` files from breadcrumbs in the browser.
        
-   **`StatusSelect` (Internal):** A helper component within the table that renders a custom dropdown for updating report statuses without triggering row expansion events.
    

### 2. State Management (`src/context`)

-   **`AuthContext.jsx`**: Provides a global state for the authenticated `project`. It handles:
    
    -   Persisting the session to `localStorage`.
        
    -   Exposing `login`, `logout`, and `project` data to the entire app tree.
        
    -   Protecting routes (ensuring only authenticated users see the dashboard).
        

### 3. Services (`src/services`)

-   **`authService.js`**: Handles HTTP requests related to authentication (`/login-project`, `/create-project`).
    
-   **`reportService.js`**: Manages data fetching and updates.
    
    -   `fetchReports(apiKey)`: Retrieves the list of bugs.
        
    -   `updateReportStatus(...)`: Sends PATCH/POST requests to update the status of a specific bug on the backend.
        

### 4. Utilities (`src/utils`)

-   **`formatters.js`**: Contains pure functions for data presentation, such as formatting timestamps, mapping Status Enums to UI colors, and determining battery icons.
    

## Screenshots

### 1. The Dashboard

<img width="600" alt="portal-main" src="https://github.com/user-attachments/assets/38b7237d-c042-47a9-ad07-4f1ed3fc8c6a" />

### 2. Report Details & Actions

<img width="600" alt="portal-report" src="https://github.com/user-attachments/assets/134b4838-856b-49ef-b606-840ad84f032e" />

### 3. Project Login

<img width="600" alt="portal-login" src="https://github.com/user-attachments/assets/17b05458-1857-45a9-9712-392c891e9155" />


## License

This project is licensed under the **MIT License** - see the [LICENSE](https://github.com/OmerMel/shakelog-dashboard/blob/master/LICENSE) file for details.
