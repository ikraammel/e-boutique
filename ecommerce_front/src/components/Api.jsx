import axios from 'axios'

export const Api = axios.create({
    baseURL: 'http://localhost:8080',
})

Api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
})

export const fetchProducts = () => {
    return axios.get('http://localhost:8080/products/all')
}

export const fetchProductById = (id) => {
    return axios.get(`http://localhost:8080/products/${id}`)
}


export const fetchProductsByName = (search = '') => {
  return Api.get('/products', {
    params: { search }  
  });
};


export const fetchProductByCategory = (category) => {
    return Api.get(`/products/category/${category}`)
}

export const fetchAllCategories = () => {
  return Api.get(`/products/categories`).then(res => res.data);
}

export const addProduct = (productDto) => {
    const formData = new FormData();

    formData.append("name", productDto.name);
    formData.append("description", productDto.description);
    formData.append("price", productDto.price.toString());
    formData.append("stock", productDto.stock.toString());
    formData.append("category", productDto.category.toUpperCase());
    formData.append("color", productDto.color);

    // Tailles : envoyer soit sizeClothing, soit sizePants
    if(productDto.sizeClothing && productDto.sizeClothing.length > 0) {
        productDto.sizeClothing.forEach(s => formData.append("sizeClothing", s));
    } else if(productDto.sizePants && productDto.sizePants.length > 0) {
        productDto.sizePants.forEach(s => formData.append("sizePants", s));
    }

    // Fichiers
    if(productDto.files) {
        productDto.files.forEach(f => formData.append("files", f));
    }

    return Api.post('/products', formData);
};

export const updateProduct = (id,productDto) => {
  return Api.patch(`/products/${id}`,null,{
    params: productDto
  })
}

export const deleteProduct = (id) => {
  return Api.delete(`/products/${id}`)
}

export const addVariant = (productId, formData) => {
  return Api.post(`/products/${productId}/variants`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
};


export const updateVariant = (productId, variantId, data) => {
  return Api.patch(`products/${productId}/${variantId}`, data, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

// POST pour uploader les images
export const addVariantImages = (variantId, data) => {
  return Api.post(`products/variants/${variantId}/images`, data, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const deleteVariant = (productId,variantId) => {
  return Api.delete(`/products/${productId}/variants/${variantId}`)
}

export const getVariantById = (productId,variantId) => {
  return Api.get(`/products/${productId}/variants/${variantId}`)
}

export const deleteVariantImage = async (variantId, index) => {
  return Api.delete(`products/variants/${variantId}/images/${index}`);
};

export const addToCart = async(userId,variantId,quantity) => {
  return Api.post(`cart/${userId}/add?variantId=${variantId}&quantity=${quantity}`);
}

export const getCartItems = async(userId) => {
  return Api.get(`cart/${userId}/items`)
}

export const removeVariantFromCart = async(variantId,userId) => {
  return Api.delete(`/cart/${variantId}?userId=${userId}`)
}

export const clearCart = async(userId) => {
  return Api.delete(`/cart/user/${userId}`)
}

export const updateQuantity = async(userId,variantId,quantity) => {
  return Api.put(`/cart/update?userId=${userId}&variantId=${variantId}&quantity=${quantity}`)
}

export const changeVariant = async(userId,oldVariantId,newVariantId) => {
  return Api.put(`/cart/change?userId=${userId}&oldVariantId=${oldVariantId}&newVariantId=${newVariantId}`)
}

export const authenticate = async (email, password) => {
  return Api.post('/api/auth/authenticate', {
    email,
    password
  })
}

export const registrate = async(firstName,lastName,email,password) => {
  return Api.post('/api/auth/registrate', {
    firstName,
    lastName,
    email,
    password
  })
}

export const dashboardService = {
  getStats: () => Api.get('/api/admin/dashboard/stats'),
  
  getSalesChart: (days = 7) => Api.get('/api/admin/dashboard/sales-chart', {
    params: { days }
  }),
  
  getTopProducts: (limit = 5) => Api.get('/api/admin/dashboard/top-products', {
    params: { limit }
  }),
  
  getRecentOrders: (limit = 5) => Api.get('/api/admin/dashboard/recent-orders', {
    params: { limit }
  }),
  
  getCategorySales: () => Api.get('/api/admin/dashboard/category-sales')
};
