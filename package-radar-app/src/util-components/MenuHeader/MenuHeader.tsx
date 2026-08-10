import { Link, useLocation } from "react-router";
import platyPulse_svg from "../../../public/Platypulse_logo.svg"
import "./MenuHeader.css"
import SearchBar from "../SearchBarComponents/SearchBar";

function MenuHeader(){
    const location = useLocation();
    return(
        <div className="menu-header-container">
            <div className="nav-section">
                <div className="menu-header-search-bar">
                    {location.pathname != "/home" && <SearchBar/>}
                </div>
                <Link to="/home" className="nav-button">
                    <p>Home</p>
                </Link>
            </div>
        </div>
    );
}

export default MenuHeader;