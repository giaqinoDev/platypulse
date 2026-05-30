function EcosystemDropdown(){
    return(
        <div>
            <form onSubmit={handleSubmit}>
                <div className="search-field-container">
                    <IoSearchOutline className="search-icon"/>
                    <input
                        className="package-search-bar" 
                        type="text" 
                        placeholder="Analyze Package..."
                        name="searchTerm" 
                        required
                        value={searchTerm}
                        onChange={(event) => {setSearchTerm(event.target.value)}}/>
                </div>
                <EcosystemDropdown/>
            </form>
        </div>
    );
}

export default EcosystemDropdown;