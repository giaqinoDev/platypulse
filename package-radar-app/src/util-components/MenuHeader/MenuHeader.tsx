import { Link, useLocation } from "react-router";
import platyPulse_svg from "../../../public/Platypulse_logo.svg"
import "./MenuHeader.css"
import SearchBar from "../SearchBarComponents/SearchBar";

function MenuHeader(){
    const location = useLocation();
    return(
        <div className="menu-header-container">
            <Link to="/home">
                <img src={platyPulse_svg} className="platy-pulse-svg"></img>
            </Link>
            <div className="nav-section">
                {location.pathname != "/home" && <SearchBar/>}
                <Link to="/home" className="nav-button">
                    <p>Home</p>
                </Link>
                {/*<Link to="/about" className="nav-button">
                    <p>About</p>
                </Link>
                */}
            </div>
        </div>
    );
}

export default MenuHeader;